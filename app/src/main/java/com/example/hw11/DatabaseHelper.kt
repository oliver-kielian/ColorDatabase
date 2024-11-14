package com.example.hw11

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object {
        private const val DATABASE_NAME = "colors.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(p0: SQLiteDatabase?) {
        val query = """
            CREATE TABLE color(
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            red INT,
            green INT,
            blue INT,
            name STRING,
            favorite BOOLEAN
            )
        """.trimIndent()
        p0?.execSQL(query)
        val db = this.readableDatabase
        db.delete("color", null, null)
    }
    fun delete()
    {
        val db = this.readableDatabase
        db.delete("color", null, null)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun insertItem(red:Int, green:Int, blue:Int)
    {
        val db = this.readableDatabase

        val colorValues = ContentValues().apply {
            put("red", red)
            put("blue", blue)
            put("green", green)
        }

        db?.insert("color", null, colorValues)
    }

    fun getAllItems(): Cursor {
        val db = this.readableDatabase

        val cursor = db.query(
            "color",
            arrayOf("_id", "red", "green", "blue", "favorite"),
            null,
            null,
            null,
            null,
            null,
            null
        )

        return cursor
    }

    fun sortColors(category: String): Cursor {
        val db = this.readableDatabase

        val cursor = db.query(
            "color",
            arrayOf("_id", "red", "green", "blue", "favorite"),
            null,
            null,
            null,
            null,
            "$category DESC",
            null
        )

        return cursor
    }

    fun setAsFavorite(id: Int)
    {
        val db = this.readableDatabase

        val favorite = ContentValues().apply{
            put("favorite", 1)
        }
        db?.update("color", favorite, "_id = ?", arrayOf(id.toString()))
    }

    fun removeFavorite(id: Int) {
        val db = this.readableDatabase

        val favorite = ContentValues().apply{
            put("favorite", 0)
        }
        db?.update("color", favorite, "_id = ?", arrayOf(id.toString()))
    }
}