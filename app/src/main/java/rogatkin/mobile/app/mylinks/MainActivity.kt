package rogatkin.mobile.app.mylinks

import android.content.Intent
import android.os.Bundle
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
import rogatkin.mobile.app.mylinks.model.Model
import rogatkin.mobile.app.mylinks.model.line
import rogatkin.mobile.app.mylinks.model.lines
import rogatkin.mobile.app.mylinks.model.setting
import rogatkin.mobile.app.mylinks.ui.SettingsActivity
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {

    lateinit var model: Model

    val scheduler = Timer()

    companion object {
        const val server_url_base =
            "http://dmitriy-desktop:8080/weblinks"
        const val TAG = "Links"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                speakWhatHappened(null)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    fun speakWhatHappened(since: Date?) {
        // found all records changed from since, if null, then all
// val links = model.load(null, line::class.java, null,  "id", "name", "url", "description")
        val lines = lines()
        lines.endpoint =
            PreferenceManager.getDefaultSharedPreferences(this).getString("host", server_url_base)
        lines.lines =
            model.load(null, line::class.java, null, "group_id", "created_on")?.toTypedArray()
        // "user-agent" header should be set to "mobile:android" ...
        model.web.put(lines.lines, lines, { ls ->
            lines.lines = model.web.putJSONArray(ls.response, line(), true)
            // store lines back to db which were changed
            lines.lines.forEach {
                it.global_id = it.global_id - it.id
                it.id = it.id + it.global_id
                it.global_id =  - it.global_id + it.id
                it.group_id = 1
                Log.d(TAG, it.name + " at " + it.id + "/" + it.global_id)
                if (model.validate(it))
                     model.save(it,"group_id")
            }
        }, false)
    }
}