package com.ryooku.mylittlebrick.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ryooku.mylittlebrick.R
import com.ryooku.mylittlebrick.activities.MainActivity
import com.ryooku.mylittlebrick.adapters.ProjectAdapter
import com.ryooku.mylittlebrick.dto.ProjectDTO
import com.ryooku.mylittlebrick.interfaces.ItemListener

class ProjectFragment : Fragment(), ItemListener {

    companion object {

        private const val ARG_PROJECT_ID = "projectId"

        fun newInstance(projectId: Int): ProjectFragment {
            val bundle = Bundle()
            bundle.putInt(ARG_PROJECT_ID, projectId)
            val fragment = ProjectFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var projectId: Int? = null
    private var projectDTO: ProjectDTO? = null
    private var adapter: ProjectAdapter? = null

    private var layout: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        layout = inflater.inflate(R.layout.fragment_project, container, false)
        fetchArguments()
        initRecycler()
        return layout
    }

    override fun itemSelected(position: Int) {
    }

    override fun increase(position: Int) {
        var count = projectDTO!!.itemList!![position].collectedCount
        if (count == null) count = 1 else count++
        projectDTO!!.itemList!![position].collectedCount = count
        (activity as MainActivity).database!!.changeItemCount(count, projectDTO!!.itemList!![position].id!!)
        adapter!!.notifyDataSetChanged()
    }

    override fun decrease(position: Int) {
        var count = projectDTO!!.itemList!![position].collectedCount
        if (count == null) count = 0 else count--
        projectDTO!!.itemList!![position].collectedCount = count
        (activity as MainActivity).database!!.changeItemCount(count, projectDTO!!.itemList!![position].id!!)
        adapter!!.notifyDataSetChanged()
    }

    private fun fetchArguments() {
        if (arguments == null) return
        projectId = arguments!!.getInt(ARG_PROJECT_ID)
        if (projectId == null) return
        projectDTO = (activity as MainActivity).database!!.getProjectById(projectId!!)
    }


    private fun initRecycler() {
        if (projectDTO == null) return
        val layoutManager = LinearLayoutManager(context)
        val recyclerView = layout!!.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = ProjectAdapter(projectDTO!!.itemList!!, context!!, this)
        recyclerView.adapter = adapter
    }

}
