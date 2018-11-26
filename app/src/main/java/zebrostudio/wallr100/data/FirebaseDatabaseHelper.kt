package zebrostudio.wallr100.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import io.reactivex.Single
import java.lang.NumberFormatException

interface FirebaseDatabaseHelper {

  fun getDatabase(): FirebaseDatabase
  fun fetch(databaseReference: DatabaseReference): Single<Map<String, String>>
}

class FirebaseDatabaseHelperImpl : FirebaseDatabaseHelper {

  private var firebaseDatabase: FirebaseDatabase? = null

  override fun getDatabase(): FirebaseDatabase {
    if (firebaseDatabase == null) {
      firebaseDatabase = FirebaseDatabase.getInstance()
    }
    return firebaseDatabase as FirebaseDatabase
  }

  override fun fetch(databaseReference: DatabaseReference): Single<Map<String, String>> {
    return Single.create { singleSubscriber ->
      val map = hashMapOf<String, String>()
      databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
          dataSnapshot.children.forEach {
            try {
              map[it.key.toString()] = Gson().toJson(it.value)
            } catch (e: NumberFormatException) {
              e.printStackTrace()
            }
          }
          singleSubscriber.onSuccess(map)
        }

        override fun onCancelled(databaseError: DatabaseError) {
          singleSubscriber.onError(databaseError.toException())
        }
      })
    }
  }

}