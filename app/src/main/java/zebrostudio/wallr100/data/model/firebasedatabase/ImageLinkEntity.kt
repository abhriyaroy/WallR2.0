package zebrostudio.wallr100.data.model.firebasedatabase

import com.google.gson.annotations.SerializedName

data class ImageLinkEntity(
  @SerializedName("thumbSmall") val small: String,
  @SerializedName("thumb") val thumb: String,
  @SerializedName("medium") val medium: String,
  @SerializedName("large") val large: String,
  @SerializedName("raw") val raw: String
)