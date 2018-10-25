package zebrostudio.wallr100.data.model.firebasedatabase

import com.google.gson.annotations.SerializedName

data class FirebasePicturesEntity(
  @SerializedName("imageLinks") val imageLink: ImageLinkEntity,
  @SerializedName("authorData") val author: AuthorEntity,
  @SerializedName("imageResolution") val imageResolution: ImageResolutionEntity,
  @SerializedName("imageSizes") val imageSize: ImageSizeEntity,
  @SerializedName("color") val color: String,
  @SerializedName("timeStamp") val timeStamp: Long,
  @SerializedName("referral") val referral: String
)