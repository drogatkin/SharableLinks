package rogatkin.mobile.app.mylinks.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rogatkin.mobile.app.mylinks.MainActivity
import rogatkin.mobile.app.mylinks.R
import rogatkin.mobile.app.mylinks.model.group
import rogatkin.mobile.app.mylinks.ui.line.LineViewModel
import java.util.*

class GroupFragment : Fragment() {

    private lateinit var groupViewModel: GroupViewModel

    private val vm: LineViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        groupViewModel =
            ViewModelProvider(this, ViewModelModelFactory((activity as MainActivity).model)).get(
                GroupViewModel::class.java
            )
        val root = inflater.inflate(R.layout.fragment_groups, container, false)
        setHasOptionsMenu(true)
        // val textView: TextView = root.findViewById(R.id.tx_nogroups)
        with(root.findViewById<RecyclerView>(R.id.ls_groups)!!) {
            this.setLayoutManager(LinearLayoutManager(context))
            setRecyclerViewItemTouchListener().attachToRecyclerView(this)
        }
        groupViewModel.getGroups().observe(viewLifecycleOwner, Observer {
            with(root.findViewById<RecyclerView>(R.id.ls_groups)) {
                if (this.adapter == null) {
                    this.adapter = GroupAdapter(
                        (activity as MainActivity).model.load(
                            null,
                            group::class.java,
                            null
                        )!!/*groupViewModel.getGroup().value!!*/
                    )
                } else {
                    (this.adapter as GroupAdapter).refresh(it)
                    this.adapter?.notifyDataSetChanged()
                }
            }
        })
        return root
    }

    private fun setRecyclerViewItemTouchListener() : ItemTouchHelper {

        //1
        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //3
                val recyclerView = view?.findViewById<RecyclerView>(R.id.ls_groups)
                val position = viewHolder.adapterPosition
                val group = (recyclerView!!.adapter as GroupAdapter).getItem(position)
                when(swipeDir) {
                    ItemTouchHelper.LEFT -> {
                        (activity as MainActivity).model.vc.fillView(context, activity,
                            group)
                        (recyclerView.adapter as GroupAdapter).notifyItemChanged(position)
                        //setFragmentResult("groupId", bundleOf("groupId" to group.id))
                        vm.setLines(group)
                    }
                    ItemTouchHelper.RIGHT -> {
                        if ((activity as MainActivity).model.remove(group) == 1) {
                            (recyclerView.adapter as GroupAdapter).remove(position)
                            (recyclerView.adapter as GroupAdapter).notifyItemRemoved(position)
                        } else {
                            (recyclerView.adapter as GroupAdapter).notifyItemChanged(position)
                        }
                    }
                }
               // photosList.removeAt(position)
              //  recyclerView.adapter!!.notifyItemRemoved(position)
            }
        }

        //4
        return ItemTouchHelper(itemTouchCallback)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.act_add -> {
                val group = group()
                (activity as MainActivity).model.vc.fillModel(context, activity, group)
                group.created_on = Date()
                group.modified_on = group.created_on
                (activity as MainActivity).model.save(group)
                // perhaps just hide the input field
                group.name = ""
               (activity as MainActivity).model.vc.fillView(context, activity, group)
                groupViewModel.setGroups((activity as MainActivity).model.load(
                    null,
                    rogatkin.mobile.app.mylinks.model.group::class.java,
                    null
                )!!)
                true
            }
            R.id.act_done -> {
                val group = group()
                (activity as MainActivity).model.vc.fillModel(context, activity, group)
                group.modified_on = Date()
                (activity as MainActivity).model.save(group)
                group.name = ""
                group.id = 0
                (activity as MainActivity).model.vc.fillView(context, activity, group)
                // TODO Modify just one, no full refresh
                groupViewModel.setGroups((activity as MainActivity).model.load(
                    null,
                    rogatkin.mobile.app.mylinks.model.group::class.java,
                    null
                )!!)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    inner class GroupAdapter(private val dataSet: ArrayList<group>) : // _all
        RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {

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

        fun refresh(newGroups: ArrayList<group>) {
            dataSet.clear()
            dataSet.addAll(newGroups)
        }
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