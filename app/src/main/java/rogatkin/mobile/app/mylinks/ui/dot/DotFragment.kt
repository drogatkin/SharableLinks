package rogatkin.mobile.app.mylinks.ui.dot

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import rogatkin.mobile.app.mylinks.MainActivity
import rogatkin.mobile.app.mylinks.R
import rogatkin.mobile.app.mylinks.model.SharableViewModel
import rogatkin.mobile.app.mylinks.model.line
import rogatkin.mobile.app.mylinks.model.setting
import rogatkin.mobile.app.mylinks.ui.ChangeWatcher
import java.util.*

class DotFragment : Fragment() {

    private val vm: SharableViewModel by activityViewModels()
    private val watcher = ChangeWatcher(this)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val root = inflater.inflate(R.layout.fragment_dot, container, false)
        val textView: TextView = root.findViewById(R.id.ed_linkname)
        // TODO same for URL
        textView.addTextChangedListener(watcher)
        val linkView = root.findViewById<EditText>(R.id.ed_url)
        linkView.setOnFocusChangeListener{_, hasFocus ->
            if (!hasFocus)
                requireActivity().invalidateOptionsMenu() }
        vm.getLink().observe(viewLifecycleOwner) {
            val line = if (it == null) line() else it
            (activity as MainActivity).model.vc.fillView(activity, root, line, false)
            requireActivity().invalidateOptionsMenu()
        }
        return root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        try {
            val realLine = line()
            (activity as MainActivity).model.vc.fillModel(activity, view, realLine, false)
            val line = vm.getLink().value
            menu.findItem(R.id.act_add).isVisible =
                line == null || line.group_id == 0L && vm.getLines().value != null && vm.getLines().value!!.id != 0L
            menu.findItem(R.id.act_done).isVisible =  line != null && line.group_id > 0
            menu.findItem(R.id.act_share).isVisible = menu.findItem(R.id.act_done).isVisible
        } catch (iae: IllegalArgumentException) {
            menu.findItem(R.id.act_add).isVisible = false
            menu.findItem(R.id.act_done).isVisible = false
            menu.findItem(R.id.act_share).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.act_add -> {
                val line = line()
                try {
                    (activity as MainActivity).model.vc.fillModel(activity, view, line, false)
                    line.id = 0 // add a record
                    line.global_id = 0 // also 0 for new
                    vm.getLines().value?.let{line.group_id = it.id}?: run { line.group_id = 1 }
                    line.created_on = Date()
                    line.modified_on = line.created_on
                    (activity as MainActivity).model.save(line, "global_id")
                    sync()
                    vm.setLink(line.clear())
                    watcher.reset()
                } catch (e: Exception) {
                    view?.let {
                        Snackbar.make(
                            it,
                            resources.getString(R.string.err_invalidurl),
                            10 * 1000
                        ).show()
                    }
                }
                true
            }
            R.id.act_done -> {
                val line = line()
                try {
                    (activity as MainActivity).model.vc.fillModel(activity, view, line, false)
                    if (line.id > 0) {
                        line.modified_on = Date()
                        (activity as MainActivity).model.save(line, "global_id")
                        sync()
                        vm.setLink(line.clear())
                        watcher.reset()
                    }
                } catch (e: Exception) {
                    view?.let {
                        Snackbar.make(
                            it,
                            resources.getString(R.string.err_invalidurl),
                            10 * 1000
                        ).show()
                    }
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    private fun sync() {
        val settings = setting()
        (activity as MainActivity).model.helper.loadPreferences(settings, false)
        if (!settings.server_name.isNullOrBlank() and settings.sync_enabled and ("manual" == settings.sync_mode))
            (activity as MainActivity).speakWhatHappened()
    }

}