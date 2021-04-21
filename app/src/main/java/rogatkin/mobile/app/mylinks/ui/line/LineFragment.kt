package rogatkin.mobile.app.mylinks.ui.line

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import rogatkin.mobile.app.mylinks.MainActivity
import rogatkin.mobile.app.mylinks.R

class LineFragment : Fragment() {

    private lateinit var dashboardViewModel: LineViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
                ViewModelProvider(this).get(LineViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_line, container, false)
        val textView: TextView = root.findViewById(R.id.text_noline)
        dashboardViewModel.getLines().observe(viewLifecycleOwner, Observer {
            (activity as MainActivity).model.vc.fillView(activity, root, it, true)
        })
        return root
    }
}