package com.ryooku.mylittlebrick.async

import android.os.AsyncTask
import com.ryooku.mylittlebrick.database.Database
import com.ryooku.mylittlebrick.dto.ProjectDTO

class AsyncProjectLoad(
        private val database: Database,
        private val onPreExe: () -> Unit,
        private val onPostExe: (ProjectDTO?) -> Unit)
    : AsyncTask<Int, String, ProjectDTO?>() {

    override fun onPreExecute() {
        super.onPreExecute()
        onPreExe()
    }

    override fun doInBackground(vararg params: Int?): ProjectDTO? {
        if (params.isEmpty()) return null
        val project = database.getProjectById(params[0]!!)
        val metaData = database.getItemMetaData(project!!)
        for (i in 0 until project.itemList!!.size)
            project.itemList!![i].metaData = metaData[i]
        return project
    }

    override fun onPostExecute(result: ProjectDTO?) {
        super.onPostExecute(result)
        onPostExe(result)
    }
}