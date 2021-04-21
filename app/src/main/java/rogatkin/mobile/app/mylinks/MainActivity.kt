package rogatkin.mobile.app.mylinks

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import rogatkin.mobile.app.mylinks.model.Model
import rogatkin.mobile.app.mylinks.model.group
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var model: Model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = Model(this)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_groups, R.id.navigation_line, R.id.navigation_dot))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //return super.onCreateOptionsMenu(menu)
        getMenuInflater().inflate(R.menu.top_action_menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
        R.id.act_done -> {
            val group = group()
            model.vc.fillModel(this, this, group)
            group.created_on = Date()
            group.modified_on = group.created_on
            model.save(group)
            group.name = ""
            model.vc.fillView(this, this, group)
            // update list
            true
        } else -> {
            super.onOptionsItemSelected(item)
        }
    }
}