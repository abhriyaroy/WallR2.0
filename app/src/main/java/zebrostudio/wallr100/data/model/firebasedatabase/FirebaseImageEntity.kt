package zebrostudio.wallr100.data.model.firebasedatabase

import com.google.gson.annotations.SerializedName

data class FirebaseImageEntity(
  @SerializedName("imageLinks") val imageLink: ImageLinkEntity,
  @SerializedName("authorData") val imageAuthor: ImageAuthorEntity,
  @SerializedName("imageResolutions") val imageResolution: ImageResolutionEntity,
  @SerializedName("imageSizes") val imageSize: ImageSizeEntity,
  @SerializedName("color") val color: String,
  @SerializedName("timeStamp") val timeStamp: Long,
  @SerializedName("referral") val referral: String
) {
  constructor() : this(
      ImageLinkEntity("", "", "", "", ""),
      ImageAuthorEntity("", ""),
      ImageResolutionEntity("", "", "", "", ""),
      ImageSizeEntity(0, 0, 0, 0, 0),
      "",
      0,
      "")
}