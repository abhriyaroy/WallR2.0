package zebrostudio.wallr100.android.utils

import android.graphics.Bitmap
import zebrostudio.wallr100.data.INITIAL_SIZE
import java.io.InputStream
import java.io.OutputStream

fun OutputStream.compressBitmap(
  bitmap: Bitmap,
  compressFormat: Bitmap.CompressFormat,
  compressQuality: Int
) {
  bitmap.compress(compressFormat, compressQuality, this)
  flush()
  close()
}

fun OutputStream.writeInputStreamUsingByteArray(
  inputStream: InputStream,
  byteArraySize: Int
) {
  ByteArray(byteArraySize).apply {
    var length = inputStream.read(this)
    while (length > INITIAL_SIZE) {
      write(this, INITIAL_SIZE, length)
      length = inputStream.read(this)
    }
  }
  flush()
  close()
}