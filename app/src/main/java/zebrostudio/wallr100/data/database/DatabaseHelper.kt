package zebrostudio.wallr100.data.database

import androidx.room.Room
import android.content.Context

interface DatabaseHelper {
  fun getDatabase(): WallrDatabase
}

const val DATABASE_NAME = "wallr.db"

class DatabaseHelperImpl(private val context: Context) : DatabaseHelper {
  private var databaseInstance: WallrDatabase? = null

  @Synchronized
  override fun getDatabase(): WallrDatabase {
    if (databaseInstance == null) {
      databaseInstance = Room.databaseBuilder(context.applicationContext,
        WallrDatabase::class.java,
        DATABASE_NAME).build()
    }
    return databaseInstance!!
  }

}

enum class DatabaseImageType {
  WALLPAPER,
  SEARCH,
  CRYSTALLIZED,
  EDITED,
  MINIMAL_COLOR,
  EXTERNAL
}
