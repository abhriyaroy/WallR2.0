package zebrostudio.wallr100.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Single
import zebrostudio.wallr100.data.database.entity.CollectionDatabaseImageEntity
import zebrostudio.wallr100.data.database.entity.TABLE_NAME

const val RETRIEVE_ALL_DATA_QUERY = "Select * from $TABLE_NAME"
const val RETRIEVE_DATA_USING_UID_QUERY = "Select * from $TABLE_NAME where uid = :uid"
const val DELETE_ALL_QUERY = "Delete from $TABLE_NAME"

@Dao
interface CollectionsDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(collectionDatabaseImageEntity: CollectionDatabaseImageEntity)

  @Query(RETRIEVE_ALL_DATA_QUERY)
  fun getAllData(): Single<List<CollectionDatabaseImageEntity>>

  @Query(RETRIEVE_DATA_USING_UID_QUERY)
  fun getDataUsingUid(uid: Int): Single<CollectionDatabaseImageEntity>

  @Delete
  fun deleteData(collectionDatabaseImageEntity: CollectionDatabaseImageEntity)

  @Query(DELETE_ALL_QUERY)
  fun deleteAllData()

}