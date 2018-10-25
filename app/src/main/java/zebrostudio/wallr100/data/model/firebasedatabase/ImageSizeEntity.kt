package zebrostudio.wallr100.data.model.firebasedatabase

import com.google.gson.annotations.SerializedName

data class ImageSizeEntity(
  @SerializedName("thumbSmallSize") val small: Long,
  @SerializedName("thumbSize") val thumb: Long,
  @SerializedName("mediumSize") val medium: Long,
  @SerializedName("largeSize") val large: Long,
  @SerializedName("rawSize") val raw: Long
)