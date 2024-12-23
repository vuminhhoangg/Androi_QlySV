package com.example.studentmanagement.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.studentmanagement.model.Student

class StudentDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "student_management.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "students"
        private const val ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_STUDENT_ID = "student_id"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
        CREATE TABLE $TABLE_NAME (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_STUDENT_ID TEXT NOT NULL
        )
    """.trimIndent()
        db?.execSQL(createTableQuery)
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertStudent(student: Student): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.studentName)
            put(COLUMN_STUDENT_ID, student.studentId)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllStudents(): MutableList<Student> {
        val students = mutableListOf<Student>()
        val db = readableDatabase
        val cursor = db.query(TABLE_NAME, null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_NAME))
                val studentId = getString(getColumnIndexOrThrow(COLUMN_STUDENT_ID))
                students.add(Student(name, studentId, id))
            }
            close()
        }
        return students
    }

    fun addStudent(studentId: String, studentName: String): Student? {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_STUDENT_ID, studentId)
            put(COLUMN_NAME, studentName)
        }
        val newRowId = db.insert(TABLE_NAME, null, values)
        return if (newRowId != -1L) {
            Student(studentName, studentId, newRowId.toInt())
        } else {
            null
        }
    }


    fun updateStudent(student: Student, id: Int): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, student.studentName)
            put(COLUMN_STUDENT_ID, student.studentId)
        }
        return db.update(TABLE_NAME, values, "$ID=?", arrayOf(id.toString()))
    }

    fun deleteStudent(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_NAME, "$ID=?", arrayOf(id.toString()))
    }
}
