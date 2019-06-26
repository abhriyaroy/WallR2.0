package zebrostudio.wallr100.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.gson.Gson
import io.reactivex.Single

interface FirebaseDatabaseHelper {

  fun getDatabase(): FirebaseDatabase
  fun fetch(databaseReference: DatabaseReference): Single<ArrayList<String>>
}

class FirebaseDatabaseHelperImpl(private val context: Context) : FirebaseDatabaseHelper {

  private var firebaseDatabase: FirebaseDatabase? = null

  override fun getDatabase(): FirebaseDatabase {
    if (firebaseDatabase == null) {
      FirebaseApp.initializeApp(context)
      firebaseDatabase = FirebaseDatabase.getInstance()
    }
    return firebaseDatabase as FirebaseDatabase
  }

  override fun fetch(databaseReference: DatabaseReference): Single<ArrayList<String>> {
    return Single.create { singleSubscriber ->
      val arrayList = arrayListOf<String>()
      databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
          dataSnapshot.children.forEach {
            try {
              arrayList.add(Gson().toJson(it.value))
            } catch (e: NumberFormatException) {
              e.printStackTrace()
            }
          }
          singleSubscriber.onSuccess(arrayList)
        }

        override fun onCancelled(databaseError: DatabaseError) {
          singleSubscriber.onError(databaseError.toException())
        }
      })
    }
  }

}