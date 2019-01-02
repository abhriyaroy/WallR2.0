package zebrostudio.wallr100.presentation.wallpaper.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageSizePresenterEntity(
  val small: Long,
  val thumb: Long,
  val medium: Long,
  val large: Long,
  val raw: Long
) : Parcelable