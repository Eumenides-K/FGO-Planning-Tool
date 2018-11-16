package com.ssttkkl.fgoplanningtool.ui.costitemlist

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.ssttkkl.fgoplanningtool.R
import com.ssttkkl.fgoplanningtool.ui.requirementlist.RequirementListRecViewAdapter
import com.ssttkkl.fgoplanningtool.ui.utils.CommonRecViewItemDecoration
import com.ssttkkl.fgoplanningtool.utils.toStringWithSplitter
import kotlinx.android.synthetic.main.item_costitemlist.view.*
import net.cachapa.expandablelayout.ExpandableLayout

class CostItemListAdapter(val context: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<CostItemListAdapter.ViewHolder>() {
    private var recyclerView: androidx.recyclerview.widget.RecyclerView? = null

    override fun onAttachedToRecyclerView(recView: androidx.recyclerview.widget.RecyclerView) {
        super.onAttachedToRecyclerView(recView)
        recyclerView = recView
    }

    override fun onDetachedFromRecyclerView(recView: androidx.recyclerview.widget.RecyclerView) {
        super.onDetachedFromRecyclerView(recView)
        recyclerView = null
    }

    var data: List<CostItemListEntity> = listOf()
        set(value) {
            if (field == value)
                return
            field = value
            expendedPosition = -1
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    private var listener: ((servantID: Int) -> Unit)? = null

    fun setOnServantClickListener(newListener: ((servantID: Int) -> Unit)?) {
        listener = newListener
    }

    var expendedPosition = -1
        set(value) {
            if (field >= 0)
                recyclerView?.findViewHolderForAdapterPosition(field)?.itemView?.expLayout?.collapse()
            if (field != value && value >= 0) {
                recyclerView?.findViewHolderForAdapterPosition(value)?.itemView?.expLayout?.expand()
                recyclerView?.smoothScrollToPosition(value)
            }
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_costitemlist, parent, false)).apply {
                itemView.constLayout.setOnClickListener {
                    expendedPosition = if (expendedPosition != adapterPosition) adapterPosition else -1
                }
                itemView.recView.apply {
                    adapter = RequirementListRecViewAdapter(context).apply {
                        setOnItemClickListener { _, item -> listener?.invoke(item.servantID) }
                    }
                    layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context!!, RecyclerView.VERTICAL, false)
                    addItemDecoration(CommonRecViewItemDecoration(context!!))
                }
                itemView.expLayout.setOnExpansionUpdateListener { _, state ->
                    itemView.recView.visibility = if (state == ExpandableLayout.State.COLLAPSED)
                        View.GONE
                    else
                        View.VISIBLE
                }
            }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = data[pos]
        holder.itemView.apply {
            // setup a subhead if this item is the head of its group
            if (pos == 0 || (data[pos - 1].type != data[pos].type)) {
                divider.visibility = if (pos != 0) View.VISIBLE else View.GONE
                type_textView.visibility = View.VISIBLE
                type_textView.text = item.type?.localizedName ?: ""
            } else {
                divider.visibility = View.GONE
                type_textView.visibility = View.GONE
            }

            name_textView.text = item.name
            require_textView.text = item.need.toStringWithSplitter()
            own_textView.text = item.own.toStringWithSplitter()

            if (item.need > item.own) {
                own_textView.setTextColor(ResourcesCompat.getColor(context.resources, android.R.color.holo_red_light, null))
                own_textView.typeface = Typeface.DEFAULT_BOLD
            } else {
                own_textView.setTextColor(ResourcesCompat.getColor(context.resources, android.R.color.primary_text_light, null))
                own_textView.typeface = Typeface.DEFAULT
            }

            Glide.with(context).load(item.imgFile).into(avatar_imageView)

            (recView.adapter as RequirementListRecViewAdapter).data = item.requirements
            expLayout.setExpanded(expendedPosition == pos, false)
        }
    }

    inner class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}