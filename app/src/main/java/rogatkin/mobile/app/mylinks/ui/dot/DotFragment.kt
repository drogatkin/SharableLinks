package rogatkin.mobile.app.mylinks.ui.dot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import rogatkin.mobile.app.mylinks.MainActivity
import rogatkin.mobile.app.mylinks.R
import rogatkin.mobile.app.mylinks.model.line
import rogatkin.mobile.app.mylinks.ui.line.LineViewModel
import java.util.*

class DotFragment : Fragment() {

    //private lateinit var dotViewModel: DotViewModel

    private val vm: LineViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
      //  dotViewModel =
        //    ViewModelProvider(this).get(DotViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dot, container, false)

        //dotViewModel.dot.observe(viewLifecycleOwner, Observer {
          //  (activity as MainActivity).model.vc.fillView(activity, root, it, false)
        //})
        vm.getLink().observe(viewLifecycleOwner, Observer {
            (activity as MainActivity).model.vc.fillView(activity, root, it, false)
        })
        return root
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
                line.name = ""
                line.url = ""
                line.description = ""
                (activity as MainActivity).model.vc.fillView(activity, view, line, false)
                true
            }
            R.id.act_done ->
            {
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

}