package zebrostudio.wallr100.presentation.wallpaper.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImagePresenterEntity(
  val imageLink: ImageLinkPresenterEntity,
  val author: ImageAuthorPresenterEntity,
  val imageResolution: ImageResolutionPresenterEntity,
  val imageSize: ImageSizePresenterEntity,
  val color: String,
  val timeStamp: Long,
  val referral: String
) : Parcelable