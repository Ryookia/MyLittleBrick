package com.ryooku.mylittlebrick.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ryooku.mylittlebrick.R
import com.ryooku.mylittlebrick.dto.ProjectDTO
import com.ryooku.mylittlebrick.interfaces.ProjectListListener

public class ProjectListAdapter(
        private val context: Context,
        private val projectList: ArrayList<ProjectDTO>,
        private val actionListener: ProjectListListener) : RecyclerView.Adapter<ProjectListAdapter.ViewHolder>() {


    var showArchived: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectListAdapter.ViewHolder {
        return ProjectListAdapter.ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_project_list, parent, false))
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    override fun onBindViewHolder(holder: ProjectListAdapter.ViewHolder, position: Int) {
        val project = projectList[position]
        if (!showArchived && (project.isActive == null || project.isActive == 0)) {
            holder.layout.visibility = View.GONE
            return
        } else
            holder.layout.visibility = View.VISIBLE
        holder.name.text = project.projectName
        holder.layout.setOnClickListener { actionListener.onItemSelected(position) }
        if (project.isActive == null || project.isActive == 0) {
            holder.archButton.text = context.getString(R.string.re_arch_button_desc)
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.project_archived))
        } else {
            holder.archButton.text = context.getString(R.string.arch_button_desc)
            holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.project_default))
        }
        holder.archButton.setOnClickListener { actionListener.onArchSelected(position) }
        holder.exportButton.setOnClickListener { actionListener.onExportSelected(position) }
    }

    class ViewHolder : RecyclerView.ViewHolder {
        var layout: View
        var name: TextView
        var archButton: TextView
        var exportButton: TextView

        constructor(view: View) : super(view) {
            layout = view
            name = view.findViewById(R.id.nameText)
            archButton = view.findViewById(R.id.archButton)
            exportButton = view.findViewById(R.id.exportButton)
        }
    }
}