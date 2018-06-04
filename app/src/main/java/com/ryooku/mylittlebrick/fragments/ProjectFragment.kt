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
import com.ryooku.mylittlebrick.async.AsyncProjectLoad
import com.ryooku.mylittlebrick.dto.ProjectDTO
import com.ryooku.mylittlebrick.interfaces.ItemListener
import kotlinx.android.synthetic.main.fragment_project.*

class ProjectFragment : Fragment(), ItemListener {

    companion object {

        private const val ARG_PROJECT_ID = "projectId"
        private const val SORT_NONE = 0
        private const val SORT_TYPE = 1
        private const val SORT_COLOR = 2

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
    //    private var itemMetaList: ArrayList<ItemMetaDTO>? = null
    private var adapter: ProjectAdapter? = null

    private var layout: View? = null
    private var currentSort = SORT_NONE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        layout = inflater.inflate(R.layout.fragment_project, container, false)
        fetchArguments()
        return layout
    }

    override fun itemSelected(position: Int) {
    }

    override fun increase(position: Int) {
        var count = projectDTO!!.itemList!![position].collectedCount
        if (count == null) count = 1 else count++
        projectDTO!!.itemList!![position].collectedCount = count
        (activity as MainActivity).database!!.changeItemCount(projectDTO!!.itemList!![position].id!!, count)
        adapter!!.notifyDataSetChanged()
    }

    override fun decrease(position: Int) {
        var count = projectDTO!!.itemList!![position].collectedCount
        if (count == null) count = 0 else count--
        projectDTO!!.itemList!![position].collectedCount = count
        (activity as MainActivity).database!!.changeItemCount(projectDTO!!.itemList!![position].id!!, count)
        adapter!!.notifyDataSetChanged()
    }

    private fun fetchArguments() {
        if (arguments == null) return
        projectId = arguments!!.getInt(ARG_PROJECT_ID)
        if (projectId == null) return
        AsyncProjectLoad((activity as MainActivity).database!!, this::preExe, this::postExe).execute(projectId)
    }

    private fun preExe() {
        (activity as MainActivity).showProgressLayout(R.string.project_load_desc)
    }

    private fun postExe(projectDTO: ProjectDTO?) {
        if (activity == null) return
        (activity as MainActivity).hideProgressLayout()
        this.projectDTO = projectDTO
        initLayout()
    }

    private fun sortByColor() {
        if (currentSort == SORT_COLOR) return
        currentSort = SORT_COLOR
        projectDTO!!.itemList = projectDTO!!.itemList!!.sortedWith(compareBy({ it.color!!.toInt() }))
        projectDTO!!.itemList = projectDTO!!.itemList!!.sortedWith(compareBy({ it.color!!.toInt() }))
        adapter?.setData(projectDTO!!.itemList!!)
    }

    private fun sortByType() {
        if (currentSort == SORT_TYPE) return
        currentSort = SORT_TYPE
        projectDTO!!.itemList = projectDTO!!.itemList!!.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.itemId!! }))
        adapter?.setData(projectDTO!!.itemList!!)
    }

    private fun initLayout() {
        sortByColor.setOnClickListener { sortByColor() }
        sortByType.setOnClickListener { sortByType() }
        initRecycler()
    }

    private fun initRecycler() {
        if (projectDTO == null) return
        val layoutManager = LinearLayoutManager(context)
        val recyclerView = layout!!.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = ProjectAdapter(projectDTO!!.itemList!!, context!!, (activity as MainActivity).database!!, this)
        recyclerView.adapter = adapter
    }

}
