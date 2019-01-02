package zebrostudio.wallr100.presentation.search.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UrlPresenterEntity(
  val rawImageLink: String,
  val largeImageLink: String,
  val regularImageLink: String,
  val smallImageLink: String,
  val thumbImageLink: String
) : Parcelable