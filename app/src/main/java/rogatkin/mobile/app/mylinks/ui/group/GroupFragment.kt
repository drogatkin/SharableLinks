package rogatkin.mobile.app.mylinks.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rogatkin.mobile.app.mylinks.MainActivity
import rogatkin.mobile.app.mylinks.R
import rogatkin.mobile.app.mylinks.model.group

class GroupFragment : Fragment() {

    private lateinit var groupViewModel: GroupViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        groupViewModel =
                ViewModelProvider(this, ViewModelModelFactory((activity as MainActivity).model)).get(GroupViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_groups, container, false)
        // val textView: TextView = root.findViewById(R.id.tx_nogroups)
        groupViewModel.getGroups().observe(viewLifecycleOwner, Observer {
            with(root.findViewById<RecyclerView>(R.id.ls_groups)) {
                    this?.setLayoutManager(LinearLayoutManager(context))
                    this?.adapter = GroupAdapter((activity as MainActivity).model.load(null, group::class.java, null)!!/*groupViewModel.getGroup().value!!*/)

                  //  this.adapter?.notifyDataSetChanged()
            }
        })
        return root
    }

    inner class GroupAdapter(private val dataSet: ArrayList<group>) : // _all
            RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

            fun update(element: group) {
                val ac = activity as MainActivity
                ac.model.vc.fillView(ac, view, element, true)
            }

            override fun onClick(v: View?) {
                TODO("Implement")
            }

        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.recycler_group, viewGroup, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.update(dataSet[position])
        }

        override fun getItemCount() = dataSet.size.also { showEmpty(dataSet.isEmpty()) }

        fun remove(pos: Int) {
            dataSet.removeAt(pos)
        }

        fun getItem(pos: Int): group = dataSet.get(pos)
    }

    fun showEmpty(empty: Boolean) {
        when (empty) {
            true -> {
                view?.findViewById<RecyclerView>(R.id.ls_groups)?.visibility = View.INVISIBLE
                view?.findViewById<TextView>(R.id.tx_nogroups)?.visibility = View.VISIBLE
            }
            else -> {
                view?.findViewById<RecyclerView>(R.id.ls_groups)?.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.tx_nogroups)?.visibility = View.GONE
            }
        }
    }
}