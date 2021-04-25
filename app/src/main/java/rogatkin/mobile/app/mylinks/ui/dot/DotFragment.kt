package rogatkin.mobile.app.mylinks.ui.dot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import rogatkin.mobile.app.mylinks.MainActivity
import rogatkin.mobile.app.mylinks.R

class DotFragment : Fragment() {

    private lateinit var dotViewModel: DotViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dotViewModel =
                ViewModelProvider(this).get(DotViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dot, container, false)

        dotViewModel.dot.observe(viewLifecycleOwner, Observer {
            (activity as MainActivity).model.vc.fillView(activity, root, it, true)
        })
        return root
    }
}