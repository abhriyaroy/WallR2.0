package zebrostudio.wallr100.data.api

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Single
import zebrostudio.wallr100.data.model.firebasedatabase.FirebasePicturesEntity

class FirebaseDatabaseHelper {

  private var firebaseDatabase: FirebaseDatabase? = null

  fun getDatabase(): FirebaseDatabase {
    if (firebaseDatabase == null) {
      firebaseDatabase = FirebaseDatabase.getInstance()
    }
    return firebaseDatabase as FirebaseDatabase
  }

  fun fetch(databaseReference: DatabaseReference): Single<List<FirebasePicturesEntity>> {
    return Single.create { singleSubscriber ->
      val imageList = arrayListOf<FirebasePicturesEntity>()
      databaseReference.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
          dataSnapshot.children.forEach {
            imageList.add(it.getValue(FirebasePicturesEntity::class.java)!!)
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