package com.ryooku.mylittlebrick.utils

import com.ryooku.mylittlebrick.dto.ProjectDTO
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class XmlExporter {
    companion object {
        fun exportToXml(project: ProjectDTO, fileDirectory: String, fileName: String) {
            val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val doc = docBuilder.newDocument()

            val rootElement = doc.createElement("INVENTORY")

            project.itemList!!.forEach {
                val itemElement = doc.createElement("ITEM")
                val itemType = doc.createElement("ITEMTYPE")
                itemType.appendChild(doc.createTextNode(it.itemType))
                val itemId = doc.createElement("ITEMID")
                itemId.appendChild(doc.createTextNode(it.itemId))
                val color = doc.createElement("COLOR")
                color.appendChild(doc.createTextNode(it.color))
                val count = doc.createElement("QTYFILLED")
                count.appendChild(doc.createTextNode(it.collectedCount.toString()))
                itemElement.appendChild(itemType)
                itemElement.appendChild(itemId)
                itemElement.appendChild(color)
                itemElement.appendChild(count)

                rootElement.appendChild(itemElement)
            }
            doc.appendChild(rootElement)

            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")

            val directory = File(fileDirectory)
            if (!directory.exists())
                directory.mkdirs()
            val file = File(directory, "$fileName.xml")
            transformer.transform(DOMSource(doc), StreamResult(file))

        }
    }
}