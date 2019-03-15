package zebrostudio.wallr100.data.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable
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
  fun getAllData(): Flowable<List<CollectionDatabaseImageEntity>>

  @Query(RETRIEVE_DATA_USING_UID_QUERY)
  fun getDataUsingUid(uid: Int): Single<CollectionDatabaseImageEntity>

  @Delete
  fun deleteData(collectionDatabaseImageEntity: CollectionDatabaseImageEntity)

  @Query(DELETE_ALL_QUERY)
  fun deleteAllData()

}