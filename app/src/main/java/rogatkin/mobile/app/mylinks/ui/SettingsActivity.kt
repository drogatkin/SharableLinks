package rogatkin.mobile.app.mylinks.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import rogatkin.mobile.app.mylinks.R
import java.text.DateFormat.getDateTimeInstance

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
           android. R.id.home -> {
                super.onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val mode : ListPreference? = findPreference("mode")
            mode?.let {
                it.summaryProvider = Preference.SummaryProvider<ListPreference> {
                  val last =  PreferenceManager
                        .getDefaultSharedPreferences(requireContext()).getLong("time", 0)
                    when(it.value) {
                        "manual" -> it.entry
                        else -> {if (last >0) {
                            val ls = getDateTimeInstance().format(last)
                            ""+it.entry+", last $ls"
                        } else
                            ""+it.entry}
                    }
                }
            }
        }
    }
}