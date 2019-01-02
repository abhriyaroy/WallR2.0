package zebrostudio.wallr100.presentation.wallpaper.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageAuthorPresenterEntity(
  val name: String,
  val profileImageLink: String
) : Parcelable