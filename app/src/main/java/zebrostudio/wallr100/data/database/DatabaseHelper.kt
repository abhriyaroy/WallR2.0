package zebrostudio.wallr100.data.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

interface DatabaseHelper {

  fun insertData(name: String, type: String, path: String, details: String): Boolean
  fun getAllData(): Cursor
  fun clearData(path: String)
  fun clearAllData()

}

const val DATABASE_NAME = "wallr.db"
const val TABLE_NAME = "collection_table"
const val INDEX_COLUMN = "INDEX"
const val NAME_COLUMN = "NAME"
const val TYPE_COLUMN = "TYPE"
const val PATH_COLUMN = "PATH"
const val DETAILS_COLUMN = "DETAILS"
const val DATABASE_VERSION = 1
const val INSERT_FAILED_CODE: Long = -1

class DatabaseHelperImpl(
  context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null,
    DATABASE_VERSION),
    DatabaseHelper {

  override fun onCreate(db: SQLiteDatabase) {
    db.execSQL(
        "create table $TABLE_NAME (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, TYPE TEXT, PATH TEXT, DETAILS TEXT)")
  }

  override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    onCreate(db)
  }

  override fun insertData(name: String, type: String, path: String, details: String): Boolean {
    writableDatabase.apply {
      val contentValues = ContentValues()
      contentValues.put(NAME_COLUMN, name)
      contentValues.put(TYPE_COLUMN, type)
      contentValues.put(PATH_COLUMN, path)
      contentValues.put(DETAILS_COLUMN, details)
      val result = insert(TABLE_NAME, null, contentValues)
      return result != INSERT_FAILED_CODE
    }
  }

  override fun getAllData(): Cursor {
    writableDatabase.let {
      return it.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }
  }

  override fun clearData(path: String) {
    writableDatabase.execSQL("DELETE FROM $TABLE_NAME WHERE $PATH_COLUMN= '$path'")
  }

  override fun clearAllData() {
    writableDatabase.execSQL("DELETE FROM $TABLE_NAME")
  }

}