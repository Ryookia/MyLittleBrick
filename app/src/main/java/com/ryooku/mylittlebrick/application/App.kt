package com.ryooku.mylittlebrick.application

import android.app.Application
import com.ryooku.mylittlebrick.database.Database
import com.ryooku.mylittlebrick.database.DbHelper

class App : Application() {

    private var databaseManager: DbHelper? = null

    override fun onCreate() {
        super.onCreate()
        initDBManager()
    }

    private fun initDBManager() {
        val path = filesDir.absolutePath + AppConfig.DB_PATH
        databaseManager = DbHelper(this, path, AppConfig.DB_NAME, AppConfig.DB_VERSION)
        try {
            databaseManager!!.create()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
////            databaseManager!!.readableDatabase
            databaseManager!!.openDatabase()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        val database = Database(databaseManager!!)
        println(databaseManager!!.database.toString())
        database.getCategories()

    }

}
