package zebrostudio.wallr100.data

import com.google.gson.Gson
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity

interface GsonDataHelper {
  fun getString(T: Any): String
  fun getImageEntity(string: String): FirebaseImageEntity
}

class GsonDataHelperImpl : GsonDataHelper {

  private val gson = Gson()

  override fun getString(input: Any): String {
    return gson.toJson(input)
  }

  override fun getImageEntity(string: String): FirebaseImageEntity {
    return gson.fromJson(string, FirebaseImageEntity::class.java)
  }
}