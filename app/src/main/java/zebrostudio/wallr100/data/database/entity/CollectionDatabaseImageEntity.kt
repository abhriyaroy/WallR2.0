package zebrostudio.wallr100.data.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

const val TABLE_NAME = "collection_table"
const val NAME_COLUMN = "NAME"
const val TYPE_COLUMN = "TYPE"
const val PATH_COLUMN = "PATH"
const val DETAILS_COLUMN = "DETAILS"

@Entity(tableName = TABLE_NAME)
data class CollectionDatabaseImageEntity(
  @PrimaryKey(autoGenerate = true)
  val uid: Long,

  @ColumnInfo(name = NAME_COLUMN)
  val name: String,

  @ColumnInfo(name = TYPE_COLUMN)
  val type: Int,

  @ColumnInfo(name = PATH_COLUMN)
  val path: String,

  @ColumnInfo(name = DETAILS_COLUMN)
  val details: String
)