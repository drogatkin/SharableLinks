package rogatkin.mobile.app.mylinks.ui.line

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import rogatkin.mobile.app.mylinks.MainActivity
import rogatkin.mobile.app.mylinks.R
import rogatkin.mobile.app.mylinks.model.SharableViewModel
import rogatkin.mobile.app.mylinks.model.line
import java.util.*

class LineFragment : Fragment() {

    //private lateinit var lineViewModel: LineViewModel

    private val vm: SharableViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // lineViewModel =
        //           ViewModelProvider(this).get(LineViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_line, container, false)

        vm.getLines().observe(viewLifecycleOwner, Observer {
            (activity as MainActivity).model.vc.fillView(activity, root, it, true)

            with(root.findViewById<RecyclerView>(R.id.ls_links)) {
                this.setLayoutManager(LinearLayoutManager(context))
                val ln = line()
                ln.group_id = it.id
                if (this.adapter == null) {
                    this.adapter = LineAdapter(
                        (activity as MainActivity).model.load(
                            (activity as MainActivity).model.whereVals(ln, "group_id"),
                            line::class.java,
                            null
                        )!!
                    )
                } else { // more likelly it never happens
                    (this.adapter as LineFragment.LineAdapter).refresh(
                        (activity as MainActivity).model.load(
                            (activity as MainActivity).model.whereVals(ln, "group_id"),
                            line::class.java,
                            null
                        )!!
                    )
                    this.adapter?.notifyDataSetChanged()
                }
            }
        })
        vm.getLines().value?.let { vm.setLines(it) }
        /*setFragmentResultListener("groupId") { requestKey, bundle ->
            val group = group()
            group.id = bundle.getLong("groupId")
                (activity as MainActivity).model.load((activity as MainActivity).model.whereVals(group, "id"), group, "created_on")
            (activity as MainActivity).model.vc.fillView(context, view, group, true)
            vm.setLines(group)
        }*/

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //3
                val recyclerView = view?.findViewById<RecyclerView>(R.id.ls_links)
                val position = viewHolder.adapterPosition
                val line = (recyclerView!!.adapter as LineFragment.LineAdapter).getItem(position)
                when(swipeDir) {
                    ItemTouchHelper.LEFT -> {
                       // (activity as MainActivity).model.vc.fillView(context, activity,
                         //   line)
                        (recyclerView.adapter as LineFragment.LineAdapter).notifyItemChanged(position)
                        vm.setLink(line)
                    }
                    ItemTouchHelper.RIGHT -> {
                        if ((activity as MainActivity).model.remove(line) == 1) {
                            (recyclerView.adapter as LineFragment.LineAdapter).remove(position)
                            (recyclerView.adapter as LineFragment.LineAdapter).notifyItemRemoved(position)
                        } else {
                            (recyclerView.adapter as LineFragment.LineAdapter).notifyItemChanged(position)
                        }
                    }
                }
            }
        }
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(root.findViewById<RecyclerView>(R.id.ls_links))
        return root
    }

    fun showEmpty(empty: Boolean) {
        when (empty) {
            true -> {
                view?.findViewById<RecyclerView>(R.id.ls_links)?.visibility = View.INVISIBLE
                view?.findViewById<TextView>(R.id.text_noline)?.visibility = View.VISIBLE
            }
            else -> {
                view?.findViewById<RecyclerView>(R.id.ls_links)?.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.text_noline)?.visibility = View.GONE
            }
        }
    }

    inner class LineAdapter(private val dataSet: ArrayList<line>) :
        RecyclerView.Adapter<LineAdapter.ViewHolder>() {

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {

            fun update(element: line) {
                when (element.name) {
                    null -> element.name = "not set"
                    "" -> "blank"
                }

                val ac = activity as MainActivity
                ac.model.vc.fillView(ac, view, element, true)
            }

            override fun onClick(v: View?) {
                TODO("Implement")
            }

        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recycler_line, viewGroup, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
            viewHolder.update(dataSet[position])
        }

        override fun getItemCount() = dataSet.size.also { showEmpty(dataSet.isEmpty()) }

        fun remove(pos: Int) {
            dataSet.removeAt(pos)
        }

        fun getItem(pos: Int): line = dataSet.get(pos)

        fun refresh(newLines: ArrayList<line>) {
            dataSet.clear()
            dataSet.addAll(newLines)
        }
    }
}