package zebrostudio.wallr100.domain.model.imagedownload

import android.graphics.Bitmap

data class ImageDownloadModel(
  var progress: Long,
  val imageBitmap: Bitmap?
)