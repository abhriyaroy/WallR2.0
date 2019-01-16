package zebrostudio.wallr100.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.Observable
import zebrostudio.wallr100.data.exception.ImageDownloadException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
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
  private val byteArraySize = 2048

  override fun fetchImage(link: String): Observable<Long> {
    return Observable.create {
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
        inputStream = connection.inputStream
        outputStream = FileOutputStream(fileHandler.getCacheFile())
        val data = ByteArray(byteArraySize)
        var count: Int = inputStream.read(data)
        var read: Long = 0
        while (count != -1) {
          read += count.toLong()
          outputStream.write(data, 0, count)
          if (shouldContinueFetchingImage) {
            val progress = (read * 100 / length)
            it.onNext(progress)
            count = inputStream.read(data)
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
      } catch (e: IOException) {
        it.onError(e)
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