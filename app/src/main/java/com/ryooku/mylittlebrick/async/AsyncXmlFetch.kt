package com.ryooku.mylittlebrick.async

import android.os.AsyncTask
import android.util.Log
import com.ryooku.mylittlebrick.dto.ItemDTO
import com.ryooku.mylittlebrick.dto.ProjectDTO
import okhttp3.OkHttpClient
import okhttp3.Request
import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

class AsyncXmlFetch(private val preAction: () -> Unit, private val postAction: (ProjectDTO?) -> Unit) : AsyncTask<String, ItemDTO, ProjectDTO>() {

    override fun onPreExecute() {
        super.onPreExecute()
        preAction()
    }

    override fun doInBackground(vararg params: String?): ProjectDTO? {
        Request.Builder().url("${params[0]}/${params[1]}.xml").build().also {
            val client = OkHttpClient()
            val response = client.newCall(it).execute()
            if (!response.isSuccessful) {
                Log.e("ProjectListFragment", response.body().toString())
                return null
            }

            val body = response.body()!!
            val inStream = body.byteStream()

            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(inStream)

            val element = doc.documentElement
            element.normalize()

            val nList = doc.getElementsByTagName("ITEM")
            val resultList = mutableListOf<ItemDTO>()
            val projectDTO = ProjectDTO()
            projectDTO.projectId = Integer.valueOf(params[1])
            projectDTO.projectName = params[2]
            projectDTO.itemList = resultList

            for (i in 0 until nList.length) {
                val node = nList.item(i)
                if (node.nodeType === Node.ELEMENT_NODE) {
                    val element2 = node as Element
                    val newItem = ItemDTO()
                    newItem.itemType = getValue("ITEMTYPE", element2)
                    newItem.itemId = getValue("ITEMID", element2)
                    newItem.desiredCount = Integer.valueOf(getValue("QTY", element2))
                    newItem.color = getValue("COLOR", element2)
                    newItem.extra = getValue("EXTRA", element2)
                    newItem.alternate = getValue("ALTERNATE", element2)
                    newItem.matchId = getValue("MATCHID", element2)
                    newItem.counterPart = getValue("COUNTERPART", element2)
                    newItem.collectedCount = 0
                    resultList.add(newItem)
                }
            }
            return projectDTO
        }
        return null
    }

    override fun onPostExecute(result: ProjectDTO?) {
        super.onPostExecute(result)
        postAction(result)
    }

    private fun getValue(tag: String, element: Element): String {
        val nodeList = element.getElementsByTagName(tag).item(0).childNodes
        val node = nodeList.item(0)
        return node.nodeValue
    }
}