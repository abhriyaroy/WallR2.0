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
}

class FirebaseDatabaseHelperImpl : FirebaseDatabaseHelper {

  private var firebaseDatabase: FirebaseDatabase? = null

  override fun getDatabase(): FirebaseDatabase {
    if (firebaseDatabase == null) {
      firebaseDatabase = FirebaseDatabase.getInstance()
    }
    return firebaseDatabase as FirebaseDatabase
  }

  override fun fetch(databaseReference: DatabaseReference): Single<List<FirebaseImageEntity>> {
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

}