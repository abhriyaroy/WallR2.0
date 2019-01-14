package zebrostudio.wallr100.presentation.wallpaper.model

import java.io.Serializable

data class ImageResolutionPresenterEntity(
  val small: String,
  val thumb: String,
  val medium: String,
  val large: String,
  val raw: String
): Serializable