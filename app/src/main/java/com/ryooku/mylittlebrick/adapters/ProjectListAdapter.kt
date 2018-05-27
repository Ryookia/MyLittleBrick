package com.ryooku.mylittlebrick.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ryooku.mylittlebrick.R
import com.ryooku.mylittlebrick.dto.ProjectDTO
import com.ryooku.mylittlebrick.interfaces.ProjectListListener

public class ProjectListAdapter(
        private val projectList: List<ProjectDTO>,
        private val actionListener: ProjectListListener) : RecyclerView.Adapter<ProjectListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectListAdapter.ViewHolder {
        return ProjectListAdapter.ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_project_list, parent, false))
    }

    override fun getItemCount(): Int {
        return projectList.size
    }

    override fun onBindViewHolder(holder: ProjectListAdapter.ViewHolder, position: Int) {
        val project = projectList[position]
        holder.name.text = project.projectName
        holder.archButton.setOnClickListener { actionListener.onArchSelected(position) }
        holder.exportButton.setOnClickListener { actionListener.onExportSelected(position) }
    }

    public class ViewHolder : RecyclerView.ViewHolder {
        public lateinit var name: TextView
        public lateinit var archButton: TextView
        public lateinit var exportButton: TextView

        constructor(view: View) : super(view) {
            name = view.findViewById(R.id.nameText)
            archButton = view.findViewById(R.id.archButton)
            exportButton = view.findViewById(R.id.exportButton)
        }
    }
}