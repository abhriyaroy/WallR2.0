package zebrostudio.wallr100.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Single
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity

interface FirebaseDatabaseHelper {

  fun getDatabase(): FirebaseDatabase
  fun fetch(databaseReference: DatabaseReference): Single<List<FirebaseImageEntity>>
  fun getExploreNodeReference(): DatabaseReference
  fun getCollectionsNodeReference(): DatabaseReference
  fun getCategoriesNodeReference(): DatabaseReference
}

class FirebaseDatabaseHelperImpl : FirebaseDatabaseHelper {

  private var firebaseDatabase: FirebaseDatabase? = null
  private val firebaseDatabasePath = "wallr"

  override fun getDatabase(): FirebaseDatabase {
    if (firebaseDatabase == null) {
      firebaseDatabase = FirebaseDatabase.getInstance()
    }
    return firebaseDatabase as FirebaseDatabase
  }

  override fun fetch(databaseReference: DatabaseReference): Single<List<FirebaseImageEntity>> {
    System.out.println("database called")
    return Single.create { singleSubscriber ->
      val imageList = arrayListOf<FirebaseImageEntity>()
      databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
          dataSnapshot.children.forEach {
            imageList.add(it.getValue(FirebaseImageEntity::class.java)!!)
          }
          imageList.reverse()
          singleSubscriber.onSuccess(imageList)
        }

        override fun onCancelled(databaseError: DatabaseError) {
          singleSubscriber.onError(databaseError.toException())
        }
      })
    }
  }

  override fun getExploreNodeReference() = getDatabase()
      .getReference(firebaseDatabasePath)
      .child(childPathExplore)

  override fun getCollectionsNodeReference() = getDatabase()
      .getReference(firebaseDatabasePath)
      .child(childPathCollections)

  override fun getCategoriesNodeReference() = getDatabase()
      .getReference(firebaseDatabasePath)
      .child(childPathCategories)

  companion object {
    const val childPathExplore = "explore"
    const val childPathCategories = "categories"
    const val childPathCollections = "collections"
    const val childPathRecent = "recent"
    const val childPathPopular = "popular"
    const val childPathStandout = "standout"
    const val childPathBuilding = "building"
    const val childPathFood = "food"
    const val childPathNature = "nature"
    const val childPathObject = "object"
    const val childPathPeople = "people"
    const val childPathTechnology = "technology"
    const val firebaseTimeoutDuration = 15
  }

}