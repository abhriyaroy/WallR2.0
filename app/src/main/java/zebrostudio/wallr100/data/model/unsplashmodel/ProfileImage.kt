package zebrostudio.wallr100.data.model.unsplashmodel

import com.google.gson.annotations.SerializedName

data class ProfileImage(
  @SerializedName("mediumRes") val mediumImageUrl: String
)