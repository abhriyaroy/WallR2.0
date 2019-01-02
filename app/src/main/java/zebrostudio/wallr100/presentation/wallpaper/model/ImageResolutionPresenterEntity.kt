package zebrostudio.wallr100.presentation.wallpaper.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageResolutionPresenterEntity(
  val small: String,
  val thumb: String,
  val medium: String,
  val large: String,
  val raw: String
): Parcelable