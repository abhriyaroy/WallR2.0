package zebrostudio.wallr100.presentation.detail

import com.google.gson.Gson
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

interface GsonHelper {
  fun convertToString(element: Any): String
  fun getSearchPicturesPresenterEntity(string: String): SearchPicturesPresenterEntity
  fun getImagePresenterEntity(string: String): ImagePresenterEntity
}

class GsonHelperImpl : GsonHelper {

  private val gson = Gson()

  override fun convertToString(element: Any): String {
    return gson.toJson(element)
  }

  override fun getSearchPicturesPresenterEntity(string: String): SearchPicturesPresenterEntity {
    return gson.fromJson(string, SearchPicturesPresenterEntity::class.java)
  }

  override fun getImagePresenterEntity(string: String): ImagePresenterEntity {
    return gson.fromJson(string, ImagePresenterEntity::class.java)
  }

}

