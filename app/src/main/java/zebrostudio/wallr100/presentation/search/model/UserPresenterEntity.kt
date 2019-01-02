package zebrostudio.wallr100.presentation.search.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserPresenterEntity(
  val name: String,
  val profileImageLink: String
) : Parcelable