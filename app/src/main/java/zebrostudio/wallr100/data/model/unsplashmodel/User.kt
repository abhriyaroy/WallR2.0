package zebrostudio.wallr100.data.model.unsplashmodel

import com.google.gson.annotations.SerializedName
import zebrostudio.wallr100.domain.model.ProfileImage

class User(
  val name: String,
  @SerializedName("profile_image")
  val profileImage: ProfileImage
)