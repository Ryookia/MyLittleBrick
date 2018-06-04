package com.ryooku.mylittlebrick.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.annotation.NonNull
import com.ryooku.mylittlebrick.application.AppConfig
import java.io.File
import java.io.FileOutputStream

public class DbHelper(@NonNull private val context: Context, private val databasePath: String, private val dBName: String, databaseVersion: Int)
    : SQLiteOpenHelper(context, dBName, null, databaseVersion) {

    companion object {
        const val TABLE_INVENTORY = "Inventories"
        const val INVENTORY_ID = "_id"
        const val INVENTORY_NAME = "Name"
        const val INVENTORY_ACTIVE = "Active"
        const val INVENTORY_LAST_ACCESS = "LastAccessed"
        const val TABLE_INVENTORY_ITEM = "InventoriesParts"
        const val INVENTORY_ITEM_ID = "_id"
        const val INVENTORY_ITEM_INVENTORY_ID = "InventoryID"
        const val INVENTORY_ITEM_TYPE = "TypeID"
        const val INVENTORY_ITEM_ITEM = "ItemID"
        const val INVENTORY_ITEM_COUNT_DESIRE = "QuantityInSet"
        const val INVENTORY_ITEM_COUNT = "QuantityInStore"
        const val INVENTORY_ITEM_COLOR = "ColorID"
        const val INVENTORY_ITEM_EXTRA = "Extra"
    }

    public var database: SQLiteDatabase? = null

    public fun create() {
        if (dBExists()) {

        } else {
            this.writableDatabase

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
            database = SQLiteDatabase.openDatabase(databasePath + dBName, null, SQLiteDatabase.OPEN_READWRITE)
            return true
        } catch (e: Throwable) {
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
        database = SQLiteDatabase.openDatabase(databasePath + dBName, null, SQLiteDatabase.OPEN_READWRITE)
    }


    override fun onCreate(db: SQLiteDatabase?) {}

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    override fun close() {
        database?.close()
        super.close()
    }
}