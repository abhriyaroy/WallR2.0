package zebrostudio.wallr100.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.Observable
import zebrostudio.wallr100.data.exception.ImageDownloadException
import java.io.BufferedInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

interface ImageHandler {

  fun fetchImage(link: String): Observable<Long>
  fun cancelFetchingImage()
  fun getImageBitmap(): Bitmap

}

class ImageHandlerImpl(
  private val context: Context,
  private val fileHandler: FileHandler
) : ImageHandler {

  private var shouldContinueFetchingImage: Boolean = true
  private val byteArraySize = 8192

  override fun fetchImage(link: String): Observable<Long> {
    return Observable.create {
      var downloadImageBitmap: Bitmap? = null
      var connection: HttpURLConnection? = null
      var inputStream: InputStream? = null
      var outputStream: OutputStream? = null
      shouldContinueFetchingImage = true
      try {
        connection = URL(link).openConnection() as HttpURLConnection
        connection.connect()
        val length = connection.contentLength
        if (length <= 0) {
          it.onError(ImageDownloadException())
        }
        inputStream = BufferedInputStream(connection.inputStream, byteArraySize)
        outputStream = FileOutputStream(fileHandler.getCacheFile())
        val bytes = ByteArray(byteArraySize)
        var count: Int
        var read: Long = 0
        while ((inputStream.read(bytes)) != -1) {
          count = inputStream.read(bytes)
          read += count.toLong()
          outputStream.write(bytes, 0, count)
          if (shouldContinueFetchingImage) {
            val progress = (read * 100 / length)
            it.onNext(progress)
          } else {
            try {
              connection.disconnect()
              outputStream.flush()
              outputStream.close()
              inputStream.close()
            } catch (e: IOException) {
              it.onError(e)
            }
          }
        }
      } finally {
        connection?.disconnect()
        if (outputStream != null) {
          outputStream.flush()
          outputStream.close()
        }
        inputStream?.close()
        it.onComplete()
      }
    }
  }

  override fun cancelFetchingImage() {
    shouldContinueFetchingImage = false
  }

  override fun getImageBitmap(): Bitmap {
    val options = BitmapFactory.Options()
    options.inPreferredConfig = Bitmap.Config.ARGB_8888
    return BitmapFactory.decodeFile(fileHandler.getCacheFile().path, options)
  }

  private fun createFiles() {

  }

}