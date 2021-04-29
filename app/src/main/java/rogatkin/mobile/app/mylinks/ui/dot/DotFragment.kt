package rogatkin.mobile.app.mylinks.ui.dot

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import rogatkin.mobile.app.mylinks.MainActivity
import rogatkin.mobile.app.mylinks.R
import rogatkin.mobile.app.mylinks.model.SharableViewModel
import rogatkin.mobile.app.mylinks.model.line
import rogatkin.mobile.app.mylinks.ui.ChangeWacher
import java.util.*

class DotFragment : Fragment() {

    private val vm: SharableViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val root = inflater.inflate(R.layout.fragment_dot, container, false)
        val textView: TextView = root.findViewById(R.id.ed_linkname)
        textView.addTextChangedListener(ChangeWacher(this))
        vm.getLink().observe(viewLifecycleOwner, Observer {
            (activity as MainActivity).model.vc.fillView(activity, root, it, false)
            requireActivity().invalidateOptionsMenu()
        })
        return root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val line = line()
        try {
            (activity as MainActivity).model.vc.fillModel(context, activity, line)
            menu.findItem(R.id.act_add).isVisible = true//!line.name.isEmpty() && line.id == 0L && vm.getLines() != null && vm.getLines().value != null && vm.getLines().value!!.id != 0L
            menu.findItem(R.id.act_done).isVisible = !line.name.isEmpty() && line.id > 0
        } catch(iae:IllegalArgumentException) {
            menu.findItem(R.id.act_add).setVisible(true)
            menu.findItem(R.id.act_done).setVisible(false)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.act_add -> {
                val line = line()
                (activity as MainActivity).model.vc.fillModel(activity, view, line, false)
                line.group_id = vm.getLines().value!!.id
                line.created_on = Date()
                line.modified_on = line.created_on
                (activity as MainActivity).model.save(line)
                vm.setLink(line.clear())
                true
            }
            R.id.act_done ->
            {
                val line = line()
                (activity as MainActivity).model.vc.fillModel(activity, view, line, false)
                if (line.id > 0) {
                    line.modified_on = Date()
                    (activity as MainActivity).model.save(line)
                    vm.setLink(line.clear())
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

}