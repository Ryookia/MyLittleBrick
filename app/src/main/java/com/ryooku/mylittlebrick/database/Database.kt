package com.ryooku.mylittlebrick.database;

import android.content.ContentValues
import android.database.Cursor
import com.ryooku.mylittlebrick.dto.ItemDTO
import com.ryooku.mylittlebrick.dto.ProjectDTO

public class Database(private val dbHelper: DbHelper) {

    public fun getCategories() {
        val cur = dbHelper.database!!.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
        if (cur.moveToFirst()) {
            while (!cur.isAfterLast) {
                println(cur.getString(0))
                cur.moveToNext()
            }
        }
        cur.close()
        println("kek")
        val cursor = dbHelper.database!!.query("Categories", arrayOf("_id", "Code", "Name"), null, null, null, null, null)
        while (!cursor.isLast) {
            println(cursor.getString(2))
            cursor.moveToNext()
        }
        cursor.close()
    }

    fun printTables() {
        val cursor = dbHelper.database!!.rawQuery("SELECT name FROM sqlite_master WHERE type='table';", null)
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            println(cursor.getString(0))
            val inCursor = dbHelper.database!!.query(cursor.getString(0), null, null, null, null, null, null)
            var columns = inCursor.columnNames
            columns.forEach { println("${cursor.getString(0)}: $it") }
            inCursor.close()
            cursor.moveToNext()
        }
        cursor.close()
    }

    private fun getMaxItemId(): Int {
        val cursor = dbHelper.database!!.rawQuery("SELECT MAX(${DbHelper.INVENTORY_ITEM_ID}) FROM ${DbHelper.TABLE_INVENTORY_ITEM};", null)
        var result = 0
        if (cursor != null) {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                result = cursor.getInt(0);
                cursor.moveToNext();
            }
            cursor.close()
        }
        return result
    }

    fun getProjectListWithoutItems(): List<ProjectDTO> {
        val db = dbHelper.database
        val cursor = db!!.rawQuery("SELECT * FROM ${DbHelper.TABLE_INVENTORY};", null)
        val result = mutableListOf<ProjectDTO>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val project = ProjectDTO()
            project.projectName = cursor.getString(cursor.getColumnIndex(DbHelper.INVENTORY_NAME))
            project.projectId = cursor.getInt(cursor.getColumnIndex(DbHelper.INVENTORY_ID))
            project.isActive = cursor.getInt(cursor.getColumnIndex(DbHelper.INVENTORY_ACTIVE))
            result.add(project)
            cursor.moveToNext()
        }
        cursor.close()
        return result
    }

    fun getProjectList(): List<ProjectDTO> {
        val db = dbHelper.database
        val cursor = db!!.rawQuery("SELECT * FROM ${DbHelper.TABLE_INVENTORY};", null)
        val result = mutableListOf<ProjectDTO>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val project = ProjectDTO()
            project.projectName = cursor.getString(cursor.getColumnIndex(DbHelper.INVENTORY_NAME))
            project.projectId = cursor.getInt(cursor.getColumnIndex(DbHelper.INVENTORY_ID))
            project.isActive = cursor.getInt(cursor.getColumnIndex(DbHelper.INVENTORY_ACTIVE))
            project.itemList = getItemList(project.projectId!!)
            result.add(project)
            cursor.moveToNext()
        }
        cursor.close()
        return result
    }

    fun changeItemCount(id: Int, count: Int) {
        val writable = dbHelper.database
        val values = ContentValues()

        values.put(DbHelper.INVENTORY_ITEM_COUNT, count)

        writable!!.update(DbHelper.TABLE_INVENTORY_ITEM, values, "${DbHelper.INVENTORY_ITEM_ID} = ?", arrayOf(id.toString()))

    }

    fun changeArchiveStatus(projectId: Int, archStatus: Int) {
        val writable = dbHelper.database
        val values = ContentValues()

        values.put(DbHelper.INVENTORY_ACTIVE, archStatus)

        writable!!.update(DbHelper.TABLE_INVENTORY, values, "${DbHelper.INVENTORY_ID} = ?", arrayOf(projectId.toString()))
    }

    fun getProjectById(projectId: Int): ProjectDTO? {
        val db = dbHelper.database
        val cursor = db!!.rawQuery("SELECT * FROM ${DbHelper.TABLE_INVENTORY} WHERE ${DbHelper.INVENTORY_ID} = $projectId;", null)
        cursor.moveToFirst()
        var project: ProjectDTO? = null
        while (!cursor.isAfterLast) {
            project = ProjectDTO()
            project.projectName = cursor.getString(cursor.getColumnIndex(DbHelper.INVENTORY_NAME))
            project.projectId = cursor.getInt(cursor.getColumnIndex(DbHelper.INVENTORY_ID))
            project.isActive = cursor.getInt(cursor.getColumnIndex(DbHelper.INVENTORY_ACTIVE))
            project.itemList = getItemList(project.projectId!!)
            break;
        }
        cursor.close()
        return project
    }

    fun getItemList(projectId: Int): List<ItemDTO> {
        val db = dbHelper.database
        val cursor = db!!.rawQuery("Select * FROM ${DbHelper.TABLE_INVENTORY_ITEM} WHERE ${DbHelper.INVENTORY_ITEM_INVENTORY_ID} = $projectId", null)
        val result = mutableListOf<ItemDTO>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val item = ItemDTO()
            item.id = cursor.getInt(cursor.getColumnIndex(DbHelper.INVENTORY_ITEM_ID))
            item.projectId = projectId
            item.itemType = cursor.getString(cursor.getColumnIndex(DbHelper.INVENTORY_ITEM_TYPE))
            item.collectedCount = cursor.getInt(cursor.getColumnIndex(DbHelper.INVENTORY_ITEM_COUNT))
            item.desiredCount = cursor.getInt(cursor.getColumnIndex(DbHelper.INVENTORY_ITEM_COUNT_DESIRE))
            item.color = cursor.getString(cursor.getColumnIndex(DbHelper.INVENTORY_ITEM_COLOR))
            item.extra = cursor.getString(cursor.getColumnIndex(DbHelper.INVENTORY_ITEM_EXTRA))
            result.add(item)
            cursor.moveToNext()
        }
        cursor.close()
        return result;
    }

    fun addProject(project: ProjectDTO) {
        val writable = dbHelper.database
        var values = ContentValues()

        values.put(DbHelper.INVENTORY_ID, project.projectId)
        values.put(DbHelper.INVENTORY_NAME, project.projectName)
        values.put(DbHelper.INVENTORY_ACTIVE, 1)
        values.put(DbHelper.INVENTORY_LAST_ACCESS, 1)

        writable!!.insert(DbHelper.TABLE_INVENTORY, null, values)

        var maxIt = getMaxItemId()

        project.itemList!!.forEach {
            maxIt++
            values = ContentValues()
            values.put(DbHelper.INVENTORY_ITEM_ID, maxIt)
            values.put(DbHelper.INVENTORY_ITEM_INVENTORY_ID, project.projectId)
            values.put(DbHelper.INVENTORY_ITEM_TYPE, it.itemType)
            values.put(DbHelper.INVENTORY_ITEM_ITEM, it.itemId)
            values.put(DbHelper.INVENTORY_ITEM_COUNT_DESIRE, it.desiredCount)
            values.put(DbHelper.INVENTORY_ITEM_COUNT, it.collectedCount)
            values.put(DbHelper.INVENTORY_ITEM_COLOR, it.color)
            values.put(DbHelper.INVENTORY_ITEM_EXTRA, it.extra)
            writable.insert(DbHelper.TABLE_INVENTORY_ITEM, null, values)

        }
    }

    fun alreadyInDB(tableName: String, primaryKey: String, fieldValue: String): Boolean {
        val db = dbHelper.database
        var cursor: Cursor? = null
        try {
            val query = "Select * from $tableName where $primaryKey = $fieldValue"
            cursor = db!!.rawQuery(query, null)
            return cursor!!.count > 0
        } finally {
            if (cursor != null)
                cursor.close()
        }
    }

//    public fun addProject(project : ProjectDTO){
////        printTables()
//        var maxIt = getMaxItemId()
//        dbHelper.database!!.execSQL("INSERT INTO(" +
//                "${DbHelper.INVENTORY_ID},${DbHelper.INVENTORY_NAME},${DbHelper.INVENTORY_ACTIVE},${DbHelper.INVENTORY_LAST_ACCESS})" +
//                " VALUES (${project.projectId}, '${project.projectName}', 1, 1);" )
//        project.itemList!!.forEach {
//            maxIt++
//            dbHelper.database!!.execSQL("INSERT OR REPLACE INTO " +
//                    "${DbHelper.TABLE_INVENTORY_ITEM} " +
//                    "VALUES(${DbHelper.INVENTORY_ITEM_ID}, " +
//                    "${DbHelper.INVENTORY_ITEM_INVENTORY_ID}, " +
//                    "${DbHelper.INVENTORY_ITEM_TYPE}, " +
//                    "${DbHelper.INVENTORY_ITEM_ITEM}, " +
//                    "${DbHelper.INVENTORY_ITEM_COUNT_DESIRE}, " +
//                    "${DbHelper.INVENTORY_ITEM_COUNT}, " +
//                    "${DbHelper.INVENTORY_ITEM_COLOR}, " +
//                    "${DbHelper.INVENTORY_ITEM_EXTRA}, " +
//                    "VALUES(" +
//                    "'$maxIt', " +
//                    "'${project.projectId}', " +
//                    "'${it.itemType}', " +
//                    "'${it.itemId}', " +
//                    "'${it.desiredCount}', " +
//                    "'${it.collectedCount}', " +
//                    "'${it.color}', " +
//                    "'${it.extra}'")
//        }
//    }
}
