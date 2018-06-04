package com.ryooku.mylittlebrick.adapters

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.ryooku.mylittlebrick.R
import com.ryooku.mylittlebrick.dto.ItemDTO
import com.ryooku.mylittlebrick.interfaces.ItemListener


class ProjectAdapter(private val itemList: List<ItemDTO>, private val context: Context, private val listener: ItemListener)
    : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectAdapter.ViewHolder {
        return ProjectAdapter.ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_item_list, parent, false))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ProjectAdapter.ViewHolder, position: Int) {
        val item = itemList[position]
        holder.desiredCount.text = item.desiredCount.toString()
        holder.currentCount.text = item.collectedCount.toString()
        holder.color.text = item.color.toString()
        holder.id.text = item.itemId.toString()
        holder.plusButton.setOnClickListener { listener.increase(position) }
        holder.minusButton.setOnClickListener { listener.decrease(position) }
        holder.layout.setOnClickListener { listener.itemSelected(position) }

        if (item.collectedCount == 0) holder.minusButton.visibility = View.GONE
        else holder.minusButton.visibility = View.VISIBLE

        if (item.collectedCount == item.desiredCount) holder.plusButton.visibility = View.GONE
        else holder.plusButton.visibility = View.VISIBLE

        val value = item.collectedCount!! / item.desiredCount!!.toFloat()
        holder.layout.background = ColorDrawable(
                ArgbEvaluator().evaluate(value, ContextCompat.getColor(context, R.color.not_collected), ContextCompat.getColor(context, R.color.collected)) as Int
        )
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var layout: View
        var thumbnail: ImageView
        var desiredCount: TextView
        var currentCount: TextView
        var color: TextView
        var id: TextView
        var plusButton: Button
        var minusButton: Button

        constructor(view: View) : super(view) {
            layout = view
            thumbnail = view.findViewById(R.id.thumbnail)
            desiredCount = view.findViewById(R.id.desiredCount)
            currentCount = view.findViewById(R.id.currentCount)
            color = view.findViewById(R.id.color)
            id = view.findViewById(R.id.id)
            plusButton = view.findViewById(R.id.plusCount)
            minusButton = view.findViewById(R.id.minusCount)
        }
    }
}