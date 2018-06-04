package com.ryooku.mylittlebrick.application

import android.app.Application
import com.ryooku.mylittlebrick.database.Database
import com.ryooku.mylittlebrick.database.DbHelper
import com.ryooku.mylittlebrick.utils.PreferenceHelper

class App : Application() {

    var database: Database? = null
    var preferenceHelper: PreferenceHelper? = null

    override fun onCreate() {
        super.onCreate()
        initDBManager()
        preferenceHelper = PreferenceHelper(this)
    }

    private fun initDBManager() {
        val path = filesDir.absolutePath + AppConfig.DB_PATH
        val databaseManager = DbHelper(this, path, AppConfig.DB_NAME, AppConfig.DB_VERSION)
        try {
            databaseManager.create()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            databaseManager.openDatabase()
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        database = Database(databaseManager)

    }

}
