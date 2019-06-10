package zebrostudio.wallr100.domain.model.imagedownload

import android.graphics.Bitmap

data class ImageDownloadModel(
  val progress: Long,
  val imageBitmap: Bitmap?
)