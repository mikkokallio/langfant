package com.example.langfant

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.langfant.ui.lesson.Lesson

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_LESSONS_TABLE)

        // Add sample data
        val contentValues1 = ContentValues().apply {
            put("priority", 100)
            put("name", "The cat is black")
            put("image_resource", R.drawable.cat) // Assuming lesson1_image is a drawable resource
            put("description", "Learn some basic words and form simple sentences")
            put("repetitions", 5)
            put("words", "je, čovjek, pas, mačka, sretan, lijep, star, crna")
        }
        db.insert("lessons", null, contentValues1)

        val contentValues2 = ContentValues().apply {
            put("priority", 200)
            put("name", "Lesson 2")
            put("image_resource", R.drawable.ic_dashboard_black_24dp) // Assuming lesson2_image is a drawable resource
            put("description", "Description for Lesson 2")
            put("repetitions", 7)
            put("words", "word4, word5, word6")
        }
        db.insert("lessons", null, contentValues2)

        val contentValues3 = ContentValues().apply {
            put("priority", 300)
            put("name", "What the what")
            put("image_resource", R.drawable.ic_dashboard_black_24dp) // Assuming lesson2_image is a drawable resource
            put("description", "Description is all")
            put("repetitions", 15)
            put("words", "word4, word5, word6")
        }
        db.insert("lessons", null, contentValues3)

    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_LESSONS_TABLE)
        onCreate(db)
    }

    fun getAllLessons(): List<Lesson> {
        val lessonList = mutableListOf<Lesson>()

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM lessons", null)

        cursor.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val priority = cursor.getInt(cursor.getColumnIndex("priority"))
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val imageResource = cursor.getInt(cursor.getColumnIndex("image_resource"))
                val description = cursor.getString(cursor.getColumnIndex("description"))
                val repetitions = cursor.getInt(cursor.getColumnIndex("repetitions"))
                val wordsString = cursor.getString(cursor.getColumnIndex("words"))
                val words = wordsString.split(",") // Split words string into a list

                val lesson = Lesson(id, priority, name, imageResource, description, repetitions, words)
                lessonList.add(lesson)
            }
        }

        return lessonList
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Lessons.db"

        private const val SQL_CREATE_LESSONS_TABLE = "CREATE TABLE lessons (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "priority INTEGER," +
                "name TEXT," +
                "image_resource INTEGER," +
                "description TEXT," +
                "repetitions INTEGER," +
                "words TEXT)"

        private const val SQL_DELETE_LESSONS_TABLE = "DROP TABLE IF EXISTS lessons"
    }
}
