package zebrostudio.wallr100.data.model.firebasedatabase

import com.google.gson.annotations.SerializedName

data class ImageResolution(
  @SerializedName("thumbSmallRes") val small: String,
  @SerializedName("thumbRes") val thumb: String,
  @SerializedName("mediumRes") val medium: String,
  @SerializedName("largeRes") val large: String,
  @SerializedName("rawRes") val raw: String
)