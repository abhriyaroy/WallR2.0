package zebrostudio.wallr100.data.model.firebasedatabase

import com.google.gson.annotations.SerializedName

data class FirebasePicturesEntity(
  @SerializedName("imageLinks") val imageLink: ImageLink,
  @SerializedName("authorData") val author: Author,
  @SerializedName("imageResolution") val imageResolution: ImageResolution,
  @SerializedName("imageSizes") val imageSize: ImageSize,
  @SerializedName("color") val color: String,
  @SerializedName("timeStamp") val timeStamp: Long,
  @SerializedName("referral") val referral: String
)