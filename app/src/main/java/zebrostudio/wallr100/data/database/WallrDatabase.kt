package zebrostudio.wallr100.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import zebrostudio.wallr100.data.database.dao.CollectionsDao
import zebrostudio.wallr100.data.database.entity.CollectionDatabaseImageEntity

const val DATABASE_VERSION = 1

@Database(entities = [(CollectionDatabaseImageEntity::class)], version = DATABASE_VERSION)
abstract class WallrDatabase : RoomDatabase() {
  abstract fun collectionsDao(): CollectionsDao
}