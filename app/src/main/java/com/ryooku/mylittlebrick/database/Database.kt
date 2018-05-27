package com.ryooku.mylittlebrick.database;

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

}
