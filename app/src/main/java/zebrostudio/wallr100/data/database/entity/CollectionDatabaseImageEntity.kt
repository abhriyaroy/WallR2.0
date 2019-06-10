package zebrostudio.wallr100.data.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

const val TABLE_NAME = "collection_table"
const val NAME_COLUMN = "NAME"
const val PATH_COLUMN = "PATH"
const val DATA_COLUMN = "DATA"
const val TYPE_COLUMN = "TYPE"

@Entity(tableName = TABLE_NAME)
data class CollectionDatabaseImageEntity(
  @PrimaryKey(autoGenerate = true)
  val uid: Long,

  @ColumnInfo(name = NAME_COLUMN)
  val name: String,

  @ColumnInfo(name = PATH_COLUMN)
  val path: String,

  @ColumnInfo(name = DATA_COLUMN)
  val data: String,

  @ColumnInfo(name = TYPE_COLUMN)
  val type: Int
)