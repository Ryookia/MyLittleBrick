package com.ryooku.mylittlebrick.fragments


import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.ryooku.mylittlebrick.R
import com.ryooku.mylittlebrick.activities.MainActivity
import com.ryooku.mylittlebrick.adapters.ProjectListAdapter
import com.ryooku.mylittlebrick.application.AppConfig
import com.ryooku.mylittlebrick.async.AsyncImageFetch
import com.ryooku.mylittlebrick.async.AsyncProjectSave
import com.ryooku.mylittlebrick.async.AsyncXmlFetch
import com.ryooku.mylittlebrick.database.DbHelper
import com.ryooku.mylittlebrick.dialogs.*
import com.ryooku.mylittlebrick.dto.ProjectDTO
import com.ryooku.mylittlebrick.interfaces.ProjectListListener
import com.ryooku.mylittlebrick.utils.SynchronizedCounter
import com.ryooku.mylittlebrick.utils.XmlExporter
import okhttp3.OkHttpClient


class ProjectListFragment : Fragment(),
        AddProjectListener,
        ProjectListListener,
        SettingsListener,
        ExportListener {

    private var projectList: ArrayList<ProjectDTO>? = arrayListOf<ProjectDTO>()
    private var adapter: ProjectListAdapter? = null
    private var layout: View? = null;
    private var currentPosition: Int = -1
    private val counter = SynchronizedCounter()

    companion object {
        const val WRITE_EXTERNAL_CODE = 101

        fun newInstance(): ProjectListFragment {
            return ProjectListFragment()
        }
    }

    private val client = OkHttpClient()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        layout = inflater.inflate(R.layout.fragment_project_list, container, false)
        initRecycler()
        updateLayout()
        return layout
    }

    override fun onExportSelected(position: Int) {
        currentPosition = position
        if (checkSelfPermission(context!!, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_CODE)
            return
        }
        handleExport(position)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != WRITE_EXTERNAL_CODE) return
        if (permissions.isEmpty()) return
        if (permissions[0] == android.Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            handleExport(currentPosition)
    }

    override fun onArchSelected(position: Int) {
        val projectDTO = projectList!![position]
        if (projectDTO.isActive == null || projectDTO.isActive == 0) {
            projectDTO.isActive = 1
            (activity as MainActivity).database!!.changeArchiveStatus(projectDTO.projectId!!, 1)
        } else {
            projectDTO.isActive = 0
            (activity as MainActivity).database!!.changeArchiveStatus(projectDTO.projectId!!, 0)
        }
        adapter!!.notifyDataSetChanged()
    }

    override fun onItemSelected(position: Int) {
        (activity as MainActivity).fragmentHelper.setProjectFragment(projectList!![position].projectId!!)
    }

    override fun onUrlChanged(url: String) {
        (activity as MainActivity).preferenceHelper.setDefaultUrl(url)
    }

    override fun onNameSelected(name: String) {
        XmlExporter.exportToXml(projectList!![currentPosition], Environment.getExternalStorageDirectory().absolutePath, name)
        (activity as MainActivity).showMessageToast(R.string.export_successful, Toast.LENGTH_LONG)
    }

    private fun handleExport(position: Int) {
        if (currentPosition == -1) return
        currentPosition = position
        ExportDialog(activity!!, this).show()
    }


    private fun updateLayout() {
        projectList!!.clear()
        projectList!!.addAll((activity as MainActivity).database!!.getProjectList())
        initLayout()
    }

    override fun onAccept(url: String, id: String, name: String) {
        (activity as MainActivity).showProgressLayout(R.string.fetching_project)
        val asyncAction = AsyncXmlFetch(this::preFetchAction, this::postFetchAction)
        asyncAction.execute(url, id, name)
    }

    private fun preFetchAction() {
        if (activity == null) return
        (activity as MainActivity).showProgressLayout(R.string.project_fetch_start)
    }

    private fun postFetchAction(result: ProjectDTO?) {
        if (activity == null) return
        val act = (activity as MainActivity)
        if (result == null) {
            act.showMessageToast(R.string.project_fetch_fail, Toast.LENGTH_LONG)
            act.hideProgressLayout()
            return
        }
        else
            act.showMessageToast(R.string.project_fetch_succ, Toast.LENGTH_LONG)
        if (act.database!!.alreadyInDB(DbHelper.TABLE_INVENTORY, DbHelper.INVENTORY_ID, result.projectId.toString())) {
            act.showMessageToast(R.string.project_already_exists, Toast.LENGTH_LONG)
            act.hideProgressLayout()
            return
        }
        act.showProgressLayout(R.string.project_save_desc)
        AsyncProjectSave(act.database!!, this::preSave, this::postSave).execute(result)
    }

    private fun fetchImages(project: ProjectDTO?) {
        if (project == null) return
        (activity as MainActivity).showProgressLayout(R.string.fetching_images)
        var position = 0
        counter.setValue(project.itemList!!.size)
        project.itemList!!.forEach {
            var designId = it.metaData!!.designId
            position++
            if (designId == null) designId = 0
            val firstUrl: String?
            if (designId != 0)
                firstUrl = "${AppConfig.DEFAULT_IMAGE_SOURCE}$designId"
            else
                firstUrl = null
            val urlRequest: String?
            if (it.itemType != null || it.color != null || it.itemId != null)
                urlRequest = "${AppConfig.ALT_IMAGE_SOURCE}${it.itemType}/${it.color}/${it.itemId}.gif"
            else
                urlRequest = null
            val lastResort: String?
            if (it.itemId != null)
                lastResort = "${AppConfig.THIRD_IMAGE_SOURCE}${it.itemId}.jpg"
            else
                lastResort = null
            println("EXE ASYNC IMAGE FETCH")
            AsyncImageFetch(this::preFetch, this::postFetch, it.id!!)
                    .execute(3.toString(), firstUrl, urlRequest, lastResort)
        }
    }

    private fun preSave() {

    }

    private fun postSave(projectDTO: ProjectDTO?) {
        fetchImages(projectDTO)
        updateLayout()
    }

    private fun preFetch() {}
    private fun postFetch(inventoryItemId: Int, result: ByteArray?) {
        counter.decreaseCounter()
        if (result == null) return
        val act = (activity as MainActivity?)
        act?.database?.saveImageByteArray(inventoryItemId, result)
        act?.showProgressLayout("${getString(R.string.fetching_images)} pozostało: ${counter.getValue()}")
        if (counter.getValue() == 0) {
            updateLayout()
            act?.hideProgressLayout()
        }
    }

    private fun initLayout() {
        if (context == null) return
        layout!!.findViewById<FloatingActionButton>(R.id.addFab).setOnClickListener {
            AddProjectDialog(activity!!, this).show()
        }
        layout!!.findViewById<FloatingActionButton>(R.id.archFab).setOnClickListener {
            adapter!!.showArchived = !adapter!!.showArchived;
            adapter!!.notifyDataSetChanged()
        }
        layout!!.findViewById<FloatingActionButton>(R.id.settingsFab).setOnClickListener {
            SettingsDialog(activity!!, this).show()
        }
        val infoText = layout!!.findViewById<TextView>(R.id.infoText)
        if (projectList!!.isEmpty()) {
            infoText.visibility = View.VISIBLE
        } else {
            infoText.visibility = View.GONE
            adapter!!.notifyDataSetChanged()
        }

    }

    private fun initRecycler() {
        val layoutManager = LinearLayoutManager(context)
        val recyclerView = layout!!.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = ProjectListAdapter(context!!, projectList!!, this)
        recyclerView.adapter = adapter
    }
}
