package zebrostudio.wallr100.presentation.wallpaper.model

import java.io.Serializable

data class ImageSizePresenterEntity(
  val small: Long,
  val thumb: Long,
  val medium: Long,
  val large: Long,
  val raw: Long
) : Serializable