package zebrostudio.wallr100.presentation.detail.model

import android.graphics.Bitmap

data class ImageDownloadPresenterEntity(
  val progress: Long,
  val imageBitmap: Bitmap?
)