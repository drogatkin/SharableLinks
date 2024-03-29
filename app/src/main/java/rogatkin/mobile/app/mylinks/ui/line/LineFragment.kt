package rogatkin.mobile.app.mylinks.ui.line

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import rogatkin.mobile.app.mylinks.MainActivity
import rogatkin.mobile.app.mylinks.R
import rogatkin.mobile.app.mylinks.model.SharableViewModel
import rogatkin.mobile.app.mylinks.model.line
import rogatkin.mobile.app.mylinks.model.setting
import java.util.*


class LineFragment : Fragment() {

    private val vm: SharableViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_line, container, false)
        setHasOptionsMenu(true)
        vm.getLines().value?:let {
            root.findViewById<TextView>(R.id.text_noline)?.text = getString(R.string.lb_nogroup)}
        vm.getLines().observe(viewLifecycleOwner) {
            (activity as MainActivity).model.vc.fillView(activity, root, it, true)
            requireActivity().invalidateOptionsMenu()
            with(root.findViewById<RecyclerView>(R.id.ls_links)) {
                this.layoutManager = LinearLayoutManager(context)
                val settings = setting()
                (activity as MainActivity).model.helper.loadPreferences(settings, true, "divider")
                // TODO update dynamically as settings changed
                if(settings.divider)
                  this.addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
                val ln = line()
                ln.group_id = if (it == null) 1 else it.id
                val whereclause = if (ln.group_id == 1L) "<group_id" else "group_id"
                if (ln.group_id < 2) ln.group_id = 2
                if (this.adapter == null) {
                    this.adapter = LineAdapter(
                        (activity as MainActivity).model.load(
                            (activity as MainActivity).model.whereVals(ln, whereclause),
                            //null,
                            line::class.java,
                            null
                        )!!
                    )
                } else { // more likely it never happens
                    (this.adapter as LineFragment.LineAdapter).refresh(
                        (activity as MainActivity).model.load(
                            (activity as MainActivity).model.whereVals(ln, whereclause),
                            line::class.java,
                            null
                        )!!
                    )
                    this.adapter?.notifyDataSetChanged()
                }
            }
        }
        vm.getLines().value?.let { vm.setLines(it) }

        val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //3
                val recyclerView = view?.findViewById<RecyclerView>(R.id.ls_links)
                val position = viewHolder.adapterPosition
                val line = (recyclerView!!.adapter as LineFragment.LineAdapter).getItem(position)
                when (swipeDir) {
                    ItemTouchHelper.RIGHT -> {
                        // (activity as MainActivity).model.vc.fillView(context, activity,
                        //   line)
                        (recyclerView.adapter as LineFragment.LineAdapter).notifyItemChanged(
                            position
                        )
                        vm.setLink(line)
                        activity?.findViewById<BottomNavigationView>(R.id.nav_view)?.selectedItemId = R.id.navigation_dot
                    }
                    ItemTouchHelper.LEFT -> {
                        if ((activity as MainActivity).model.remove(line) == 1) {
                            (recyclerView.adapter as LineFragment.LineAdapter).remove(position)
                            (recyclerView.adapter as LineFragment.LineAdapter).notifyItemRemoved(
                                position
                            )
                            if (line.id == vm.getLink().value?.id ?: 0)
                                vm.setLink(null)
                            // clear current dot
                        } else {
                            (recyclerView.adapter as LineFragment.LineAdapter).notifyItemChanged(
                                position
                            )
                        }
                    }
                }
            }
        }
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(root.findViewById(R.id.ls_links))
        return root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.act_add).isVisible = false
        menu.findItem(R.id.act_done).isVisible = false
        menu.findItem(R.id.act_share).isVisible = false
    }

    fun showEmpty(empty: Boolean) {
        when (empty) {
            true -> {
                view?.findViewById<RecyclerView>(R.id.ls_links)?.visibility = View.INVISIBLE
                view?.findViewById<TextView>(R.id.text_noline)?.visibility = View.VISIBLE
            }
            else -> {
                view?.findViewById<RecyclerView>(R.id.ls_links)?.visibility = View.VISIBLE
                view?.findViewById<TextView>(R.id.text_noline)?.visibility = View.INVISIBLE
            }
        }
    }

    inner class LineAdapter(private val dataSet: ArrayList<line>) :
        RecyclerView.Adapter<LineAdapter.ViewHolder>() {

        inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {
            init {
                view.setOnClickListener(this)
            }

            private var urlText = ""
             fun update(element: line) {
                when (element.name) {
                    null -> element.name = "not set"
                    "" -> element.name = "blank"
                }
                if (element.url != null)
                    urlText = element.url
                val ac = activity as MainActivity
                ac.model.vc.fillView(ac, view, element, true)
                if (element.highlight) {
                    when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_YES -> {view.setBackgroundColor(Color.parseColor("#2F2F2F"))}
                        Configuration.UI_MODE_NIGHT_NO -> {view.setBackgroundColor(Color.parseColor("#F2F2F2"))}
                        Configuration.UI_MODE_NIGHT_UNDEFINED -> {view.setBackgroundColor(Color.parseColor("#F2F2F2"))}
                    }

                } else {
                    val a = TypedValue()
                    context!!.theme.resolveAttribute(android.R.attr.windowBackground, a, true)
                    if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT)
                        view.setBackgroundColor(a.data)
                    else
                        view.setBackgroundResource(a.data)
                }
                element.accessed = Date()
            }

            override fun onClick(v: View?) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(urlText.trim())
                try {
                    activity!!.startActivity(i)
                } catch (e: Exception) {
                    Snackbar.make(view, "Exception: $e", 10*1000).show()
                }
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

        fun getItem(pos: Int): line = dataSet[pos]

        fun refresh(newLines: ArrayList<line>) {
            dataSet.clear()
            dataSet.addAll(newLines)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.act_search -> {
                val inflater = LayoutInflater.from(context)
                val popupView: View = inflater.inflate(R.layout.search_popup, null)

                val width = LinearLayout.LayoutParams.WRAP_CONTENT
                val height = LinearLayout.LayoutParams.WRAP_CONTENT
                val focusable = true // lets taps outside the popup also dismiss it
                val popupWindow = PopupWindow(popupView, width, height, focusable)
                popupView.findViewById<ImageButton>(R.id.act_search).setOnClickListener { popupWindow.dismiss()
                // filter rows by search
                    val search = popupView.findViewById<EditText>(R.id.ed_search).text
                    val rv = view?.findViewById<RecyclerView>(R.id.ls_links)
                    val n = rv?.adapter?.itemCount!!
                    for(i in 0 until n) {
                        val line = (rv.adapter!! as LineAdapter).getItem(i)
                        line.highlight = search.isNotBlank() and line.name.contains(search,true)
                    }
                    (rv.adapter!! as LineAdapter).notifyDataSetChanged()
                }
                popupWindow.showAtLocation(view, Gravity.TOP, 20, 196)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
}