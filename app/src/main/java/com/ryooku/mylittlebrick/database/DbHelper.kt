package com.ryooku.mylittlebrick.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.NonNull
import com.ryooku.mylittlebrick.application.AppConfig
import java.io.File
import java.io.FileOutputStream

public class DbHelper(@NonNull private val context: Context, private val databasePath: String, private val dBName: String, databaseVersion: Int)
    : SQLiteOpenHelper(context, dBName, null, databaseVersion) {

    public var database: SQLiteDatabase? = null

    public fun create() {
        if (dBExists()) {

        } else {
            this.readableDatabase

            try {
                copyDataBase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun dBExists(): Boolean {
        var database: SQLiteDatabase? = null
        try {
            database = SQLiteDatabase.openDatabase(databasePath + dBName, null, SQLiteDatabase.OPEN_READONLY)
            return true
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }

        if (database != null) {
            database.close()
        }
        return (database != null)
    }

    private fun copyDataBase() {
        context.assets.open(AppConfig.DB_FILE_NAME).apply {
            val directory = File(databasePath)
            if (!directory.exists()) directory.mkdirs()
            val outputStream = FileOutputStream(databasePath + dBName)
            val buffer = ByteArray(1024)
            var length: Int
            do {
                length = this.read(buffer)
                if (length == 0) break
                outputStream.write(buffer, 0, length)
            } while (length > 0)
            outputStream.flush()
            outputStream.close()
            this.close()
        }
    }

    public fun openDatabase() {
        database = SQLiteDatabase.openDatabase(databasePath + dBName, null, SQLiteDatabase.OPEN_READONLY)
    }


    override fun onCreate(db: SQLiteDatabase?) {}

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    override fun close() {
        database?.close()
        super.close()
    }
}