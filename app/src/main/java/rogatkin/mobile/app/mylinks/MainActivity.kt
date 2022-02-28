package rogatkin.mobile.app.mylinks

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import rogatkin.mobile.app.mylinks.model.*
import rogatkin.mobile.app.mylinks.ui.SettingsActivity
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var model: Model

    private var android_id : String? = null

    private val viewModel: SharableViewModel by viewModels()

    private lateinit var mHandler: Handler

    companion object {
        const val server_url_base =
            "https://www.linkseverywhere.com"
        const val TAG = "Links"

        const val interval = 20L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initID( )
        mHandler = Handler(Looper.myLooper()!!)

        model = Model(this)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_groups, R.id.navigation_line, R.id.navigation_dot
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
         PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)
        periodic()
    }

    private fun initID() {
        // when app reistalled for some reason, it should act as non existent in the global db
        var salt = PreferenceManager.getDefaultSharedPreferences(this).getInt("SALT", 0)
        if (salt == 0) {
            salt = getRandomNumber(1004, 9999)
            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("SALT", salt).apply()
        }
        android_id = Settings.Secure.getString(applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID) + "-" + salt
    }

    private fun getRandomNumber(min: Int, max: Int): Int {
        return Random().nextInt((max - min) + 1) + min
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.top_action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.act_setup -> {
                //startActivity(Intent(android.provider.Settings.ACTION_SETTINGS) .setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED))
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.act_sync -> {
               // speakWhatHappened()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    fun speakWhatHappened() {
        // found all records changed from since, if null, then all
        val lines = lines()
        val since = PreferenceManager.getDefaultSharedPreferences(this).getLong("time", 1000) / 1000 // some trick to get value in seconds
        val filter = ContentValues()
        filter.put(">modified_on", since)
        lines.lines =
            model.load(filter, line::class.java, null, "group_id", "created_on")?.toTypedArray()
        // if no changes on our side, they still can be on the server
        if (lines.lines.size == 0 && false)
            return
        lines.endpoint =
            PreferenceManager.getDefaultSharedPreferences(this).getString("host", server_url_base)

        lines.modifiedSince = Date(since * 1000)
        // "user-agent" header should be set to "mobile:android" ...
        lines.user_agent += ":$android_id"  // can be app specific id, not a device
        lines.token = PreferenceManager.getDefaultSharedPreferences(this).getString("token", null)
        if (lines.token.isNullOrBlank())
            lines.token = null
        else
            lines.token = "s_token " + lines.token
        // potentially server can process a new update but returning a result can fail
        // as a result, a double records can be created, how to address that?
        // what if each update gets some transaction id? if it isn't successful then the id reminds be the same
        // so server can ignore already processed id, header X-Requested-With can be used to hold transaction id
        // a repeating transaction has to include exact same changes (anyway the case needs to be confirmed)

        model.web.put(lines.lines, lines, { ls ->
            try {
                lines.lines = model.web.putJSONArray(ls.response, line(), true)
                // store lines back to db which were changed
                lines.lines?.forEach {
                    /*it.global_id = it.global_id - it.id
                    it.id = it.id + it.global_id
                    it.global_id = -it.global_id + it.id */
                    it.global_id = it.id.apply { it.id = it.global_id } // Kotlin way?
                    it.group_id = 1 // not changing, but just in case
                    it.modified_on = Date()
                    Log.d(TAG, it.name + " at " + it.id + "/" + it.global_id)
                    if (model.validate(it))
                        model.save(it, "group_id")
                }
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putLong("time", Date().time).apply()
                // it needs to refresh only if something returned
                lines.lines?.apply {
                    // use a shareable viewmodel to update model and observers automatically do redraw
                    // since runs not on the main thread, use postValue or do withContext(Dispatchers.Main) {}
                    viewModel.getLines().value?.let { runOnUiThread { viewModel.setLines(it) } }
                }
            } catch(ae: Exception ) {
                // keep all exceptions here
            } finally {periodic()}
        }, false)
    }

    fun getWhatHappened() {
        val back = linnes_back(PreferenceManager.getDefaultSharedPreferences(this).getString("host", server_url_base))
        model.web.get(back) {
            val lines = model.web.putJSONArray(it.response, line(), true)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        Log.d(TAG, "key "+key+"changed")
        when(key) {
            "sync" , "mode", "host" -> {
                periodic()
            }
        }
    }

    private fun periodic() {
        mHandler.removeCallbacksAndMessages(null)
        val settings = setting()
        model.helper.loadPreferences(settings, false)
        if (!settings.server_name.isNullOrBlank() and settings.sync_enabled and ("automatic" == settings.sync_mode)) {
            Log.d(TAG, "rescheduling to...$interval mins")
            mHandler.postDelayed({speakWhatHappened()}, TimeUnit.MINUTES.toMillis(interval))
        }
    }
}