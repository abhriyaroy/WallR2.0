package zebrostudio.wallr100.data.model.unsplashmodel

import com.google.gson.annotations.SerializedName
import zebrostudio.wallr100.domain.model.Urls
import zebrostudio.wallr100.domain.model.User
import java.util.ArrayList

class UnsplashPicturesEntity(
  @SerializedName("id")
  val id: String,
  @SerializedName("createdAt")
  val createdAt: String,
  @SerializedName("width")
  val imageWidth: Int,
  @SerializedName("height")
  val imageHeight: Int,
  @SerializedName("color")
  val paletteColor: String,
  @SerializedName("user")
  val user: User,
  @SerializedName("likes")
  val likes: Int,
  @SerializedName("likedByUser")
  val likedByUser: Boolean,
  @SerializedName("urls")
  val imageQualityUrls: Urls,
  @SerializedName("categories")
  var categories: List<Any> = ArrayList()
)