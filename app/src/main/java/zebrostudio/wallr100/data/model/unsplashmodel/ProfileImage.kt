package zebrostudio.wallr100.data.model.unsplashmodel

import com.google.gson.annotations.SerializedName

data class ProfileImage(
  @SerializedName("medium") val mediumImageUrl: String
)