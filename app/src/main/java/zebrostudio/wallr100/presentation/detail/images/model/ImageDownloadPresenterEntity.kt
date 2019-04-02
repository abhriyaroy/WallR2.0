package zebrostudio.wallr100.presentation.detail.images.model

import android.graphics.Bitmap

data class ImageDownloadPresenterEntity(
  val progress: Long,
  val imageBitmap: Bitmap?
)