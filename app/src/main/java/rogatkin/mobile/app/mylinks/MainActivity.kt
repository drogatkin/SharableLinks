package rogatkin.mobile.app.mylinks

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {

    lateinit var model: Model

    val scheduler = Timer()

    var android_id : String? = null

    companion object {
        const val server_url_base =
            "http://dmitriy-desktop:8080/weblinks"
        const val TAG = "Links"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initID( )

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
        val settings = setting()
        model.helper.loadPreferences(settings, false)
        if (settings.check_periodic) {
            scheduler.scheduleAtFixedRate(timerTask {

            }, TimeUnit.MINUTES.toMillis(1), TimeUnit.MINUTES.toMillis(10))
        } else
            scheduler.cancel()

    }

    private fun initID() {
        // when app reistalled for some reason, it should act as non existent in the global db
        var salt = PreferenceManager.getDefaultSharedPreferences(this).getInt("SALT", 0)
        if (salt == 0) {
            salt = getRandomNumber(1004, 9999)
            PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("SALT", salt)
        }
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
            Settings.Secure.ANDROID_ID) + "-" + salt
    }

    fun getRandomNumber(min: Int, max: Int): Int {
        return Random().nextInt((max - min) + 1) + min
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        getMenuInflater().inflate(R.menu.top_action_menu, menu);
        return true;
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
                speakWhatHappened()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    fun speakWhatHappened() {
        // found all records changed from since, if null, then all
// val links = model.load(null, line::class.java, null,  "id", "name", "url", "description")
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
        lines.user_agent += ":" + android_id  // can be app specific id, not a device
        model.web.put(lines.lines, lines, { ls ->
            lines.lines = model.web.putJSONArray(ls.response, line(), true)
            // store lines back to db which were changed
            lines.lines.forEach {
                it.global_id = it.global_id - it.id
                it.id = it.id + it.global_id
                it.global_id = -it.global_id + it.id
                it.group_id = 1 // not changing, but just in case
                it.modified_on = Date()
                Log.d(TAG, it.name + " at " + it.id + "/" + it.global_id)
                if (model.validate(it))
                    model.save(it, "group_id")
            }
            PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putLong("time", Date().getTime()).apply()
        }, false)
    }

    fun getWhatHappened() {
        val back = linnes_back(PreferenceManager.getDefaultSharedPreferences(this).getString("host", server_url_base))
        model.web.get(back, {
            val lines = model.web.putJSONArray(it.response, line(), true)
        })
    }
}