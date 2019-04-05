package zebrostudio.wallr100.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.support.v4.content.FileProvider
import com.zebrostudio.wallrcustoms.lowpoly.LowPoly
import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Observable
import io.reactivex.Single
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.android.utils.compressBitmap
import zebrostudio.wallr100.android.utils.writeInputStreamUsingByteArray
import zebrostudio.wallr100.data.database.DatabaseHelper
import zebrostudio.wallr100.data.database.DatabaseImageType
import zebrostudio.wallr100.data.database.DatabaseImageType.EDITED
import zebrostudio.wallr100.data.database.entity.CollectionDatabaseImageEntity
import zebrostudio.wallr100.data.exception.AlreadyPresentInCollectionException
import zebrostudio.wallr100.data.exception.ImageDownloadException
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.GRADIENT
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.MATERIAL
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.PLASMA
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList
import java.util.Random

interface ImageHandler {
  fun isImageCached(link: String): Boolean
  fun fetchImage(link: String): Observable<Long>
  fun cancelFetchingImage()
  fun getImageBitmap(): Bitmap
  fun clearImageCache(): Completable
  fun getImageUri(): Uri
  fun getShareableUri(): Single<Uri>
  fun convertUriToBitmap(uri: Uri): Single<Bitmap>
  fun convertImageInCacheToLowpoly(): Single<Bitmap>
  fun saveCacheImageToDownloads(): Completable
  fun saveImageToCollections(data: String, type: DatabaseImageType): Completable
  fun getSingleColorBitmap(hexValue: String): Single<Bitmap>
  fun getMultiColorBitmap(
    hexValueList: List<String>,
    multiColorImageType: MultiColorImageType
  ): Single<Bitmap>
}

const val BYTE_ARRAY_SIZE = 2048
const val DOWNLOAD_PROGRESS_COMPLETED_VALUE: Long = 100
const val READ_MODE = "r"
const val BITMAP_COMPRESS_QUALITY = 100
const val INITIAL_SIZE = 0
const val UID_AUTO_INCREMENT: Long = 0
const val COLOR_BITMAP_SIZE: Int = 512

class ImageHandlerImpl(
  private val context: Context,
  private val fileHandler: FileHandler,
  private val databaseHelper: DatabaseHelper,
  private val wallpaperSetter: WallpaperSetter
) : ImageHandler {

  internal var shouldContinueFetchingImage: Boolean = true
  private var imageCacheTracker: Pair<Boolean, String> = Pair(false, "")

  override fun isImageCached(link: String): Boolean {
    return (imageCacheTracker.first && (imageCacheTracker.second == link))
  }

  override fun fetchImage(link: String): Observable<Long> {
    return Observable.create {
      var connection: HttpURLConnection? = null
      var inputStream: InputStream? = null
      var outputStream: OutputStream? = null
      shouldContinueFetchingImage = true
      imageCacheTracker = Pair(false, "")
      try {
        connection = URL(link).openConnection() as HttpURLConnection
        connection.connect()
        val length = connection.contentLength
        if (length <= INITIAL_SIZE) {
          it.onError(ImageDownloadException())
        }
        inputStream = connection.inputStream
        outputStream = FileOutputStream(fileHandler.getCacheFile())
        val data = ByteArray(BYTE_ARRAY_SIZE)
        var count: Int = inputStream.read(data)
        var read: Long = 0
        while (count != -1) {
          read += count.toLong()
          outputStream.write(data, INITIAL_SIZE, count)
          if (shouldContinueFetchingImage) {
            val progress = (read * 100 / length)
            if (progress == DOWNLOAD_PROGRESS_COMPLETED_VALUE) {
              imageCacheTracker = Pair(true, link)
            }
            it.onNext(progress)
            count = inputStream.read(data)
          } else {
            connection.disconnect()
            outputStream.flush()
            outputStream.close()
            inputStream?.close()
            break
          }
        }
      } catch (e: IOException) {
        it.onError(e)
      } finally {
        connection?.disconnect()
        outputStream?.flush()
        outputStream?.close()
        inputStream?.close()
        it.onComplete()
      }
    }
  }

  override fun cancelFetchingImage() {
    shouldContinueFetchingImage = false
  }

  override fun getImageBitmap(): Bitmap {
    return BitmapFactory.Options().let {
      it.inPreferredConfig = Bitmap.Config.ARGB_8888
      BitmapFactory.decodeFile(fileHandler.getCacheFile().path, it)
    }
  }

  override fun clearImageCache(): Completable {
    return Completable.create {
      fileHandler.deleteCacheFiles()
      imageCacheTracker = Pair(false, "")
      it.onComplete()
    }
  }

  override fun getImageUri(): Uri {
    return getImageBitmap().let { bitmap ->
      fileHandler.getCacheFile().outputStream()
          .compressBitmap(bitmap, JPEG, BITMAP_COMPRESS_QUALITY)
      Uri.fromFile(fileHandler.getCacheFile())
    }
  }

  override fun getShareableUri(): Single<Uri> {
    return Single.create { emitter ->
      getImageBitmap().let { bitmap ->
        fileHandler.getShareableFle().outputStream()
            .compressBitmap(bitmap, JPEG, BITMAP_COMPRESS_QUALITY)
        FileProvider.getUriForFile(context,
            context.applicationContext.packageName + ".provider", fileHandler.getShareableFle())
            .let {
              emitter.onSuccess(it)
            }
      }
    }
  }

  override fun convertUriToBitmap(uri: Uri): Single<Bitmap> {
    return Single.create {
      try {
        with(context.contentResolver.openFileDescriptor(uri, READ_MODE)) {
          BitmapFactory.decodeFileDescriptor(fileDescriptor).let { bitmap ->
            this?.close()
            fileHandler.getCacheFile().outputStream()
                .compressBitmap(bitmap, JPEG, BITMAP_COMPRESS_QUALITY)
            it.onSuccess(bitmap)
          }
        }
      } catch (exception: IOException) {
        it.onError(exception)
      }
    }
  }

  override fun convertImageInCacheToLowpoly(): Single<Bitmap> {
    return Single.create {
      LowPoly.generate(getImageBitmap()).let { bitmap ->
        try {
          fileHandler.getCacheFile().outputStream()
              .compressBitmap(bitmap, JPEG, BITMAP_COMPRESS_QUALITY)
          it.onSuccess(bitmap)
        } catch (exception: IOException) {
          it.onError(exception)
        }
      }
    }
  }

  override fun saveCacheImageToDownloads(): Completable {
    return Completable.create {
      try {
        fileHandler.getCacheFile().inputStream().let { inputStream ->
          fileHandler.getDownloadFile().outputStream()
              .writeInputStreamUsingByteArray(inputStream, BYTE_ARRAY_SIZE)
          inputStream.close()
          it.onComplete()
        }
      } catch (exception: IOException) {
        it.onError(exception)
      }
    }
  }

  override fun saveImageToCollections(data: String, type: DatabaseImageType): Completable {
    return Completable.create { emitter ->
      try {
        if (type == EDITED) {
          saveToCollection(emitter, data, type)
        } else {
          var isEntryAlreadyPresent = false
          databaseHelper.getDatabase().collectionsDao().getAllData().subscribe { it ->
            it.forEach {
              if (it.type == type.ordinal && it.data == data) {
                isEntryAlreadyPresent = true
              }
            }
            if (!isEntryAlreadyPresent) {
              saveToCollection(emitter, data, type)
            } else {
              emitter.onError(AlreadyPresentInCollectionException())
            }
          }
        }
      } catch (exception: IOException) {
        emitter.onError(exception)
      }
    }
  }

  private fun saveToCollection(emitter: CompletableEmitter, data: String, type: DatabaseImageType) {
    fileHandler.getCacheFile().inputStream().let { inputStream ->
      fileHandler.getCollectionsFile().let { file ->
        file.outputStream()
            .writeInputStreamUsingByteArray(inputStream, BYTE_ARRAY_SIZE)
        databaseHelper.getDatabase().collectionsDao().insert(CollectionDatabaseImageEntity(
            UID_AUTO_INCREMENT,
            file.name,
            file.path,
            data,
            type.ordinal
        ))
      }
      inputStream.close()
      emitter.onComplete()
    }
  }

  override fun getSingleColorBitmap(hexValue: String): Single<Bitmap> {
    return Single.create {
      (COLOR_BITMAP_SIZE * COLOR_BITMAP_SIZE).let {
        val bitmapArray = IntArray(it)
        val colorValue = Color.parseColor(hexValue)
        for (i in 0 until it) {
          bitmapArray[i] = colorValue
        }
        Bitmap.createBitmap(bitmapArray, COLOR_BITMAP_SIZE, COLOR_BITMAP_SIZE,
            Bitmap.Config.ARGB_8888)
      }.let { bitmap ->
        fileHandler.getCacheFile().outputStream()
            .compressBitmap(bitmap, JPEG, BITMAP_COMPRESS_QUALITY)
        it.onSuccess(bitmap)
      }
    }
  }

  override fun getMultiColorBitmap(
    hexValueList: List<String>,
    multiColorImageType: MultiColorImageType
  ): Single<Bitmap> {
    return Single.create { emitter ->
      when (multiColorImageType) {
        MATERIAL -> createMaterialBitmap(hexValueList as ArrayList<String>)
        GRADIENT -> createGradientBitmap(hexValueList as ArrayList<String>)
        PLASMA -> createPlasmaBitmap(hexValueList as ArrayList<String>)
      }.let {
        fileHandler.getCacheFile().outputStream()
            .compressBitmap(it, JPEG, BITMAP_COMPRESS_QUALITY)
        emitter.onSuccess(it)
      }
    }
  }

  private fun createMaterialBitmap(colors: ArrayList<String>): Bitmap {
    val smallHeight = wallpaperSetter.getDesiredMinimumHeight()
    val bigHeight = 2 * smallHeight
    val middleHeight = (2 * smallHeight / Math.sqrt(2.0)).toInt()
    val offset = (bigHeight - middleHeight) / 2
    val bigBitmap = Bitmap.createBitmap(bigHeight, bigHeight, Bitmap.Config.ARGB_8888)
    val colorsInt = IntArray(colors.size)
    for (i in colors.indices) {
      colorsInt[i] = Color.parseColor(colors[i])
    }
    val c = Canvas(bigBitmap)
    c.save()
    c.rotate(-45f, (c.width / 2).toFloat(), (c.height / 2).toFloat())
    val paint = Paint()
    val initStripeHeight = (middleHeight / colors.size).toFloat()
    val initShadowHeight = (middleHeight * 0.012).toFloat()
    val stripeSpread = (initStripeHeight * 0.25).toInt()  // Vary stripe height a bit.
    val shadowSpread = initShadowHeight * 0.5f  // Vary shadow thickness too.
    for (i in colors.indices.reversed()) {  // Going upwards.
      var stripeHeight: Int
      val shadowThickness: Float
      if (i == colors.size - 1) {  // Fill whole canvas with last color.
        stripeHeight = bigHeight
        shadowThickness = 0f
      } else {
        stripeHeight = Math.round((i + 1) * initStripeHeight)
        val dh = (stripeSpread * Math.random() - stripeSpread / 2).toInt()
        stripeHeight += offset + dh
        if (stripeHeight < 0) stripeHeight = 0
        if (stripeHeight > bigHeight) stripeHeight = bigHeight
        val ds = (shadowSpread * Math.random() - shadowSpread / 2).toFloat()
        shadowThickness = Math.max(1f, initShadowHeight + ds)
      }
      paint.color = colorsInt[i]
      paint.style = Paint.Style.FILL
      paint.setShadowLayer(shadowThickness, 0.0f, 0.0f, -0x1000000)
      c.drawRect(0f, 0f, bigHeight.toFloat(), stripeHeight.toFloat(), paint)
    }
    c.restore()
    val x = (c.width - smallHeight) / 2
    val y = (c.height - smallHeight) / 2
    return Bitmap.createBitmap(bigBitmap, x, y, smallHeight, smallHeight)
  }

  private fun createGradientBitmap(colors: ArrayList<String>): Bitmap {
    val height = COLOR_BITMAP_SIZE
    val wallpaperBitmap = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888)
    val colorsInt = IntArray(colors.size)
    for (i in colors.indices) {
      colorsInt[i] = Color.parseColor(colors[i])
    }
    val paint = Paint()
    val gradientShader = LinearGradient(0f, 0f, height.toFloat(), height.toFloat(), colorsInt, null,
        Shader.TileMode.CLAMP)
    val c = Canvas(wallpaperBitmap)
    paint.shader = gradientShader
    c.drawRect(0f, 0f, height.toFloat(), height.toFloat(), paint)
    return wallpaperBitmap
  }

  private fun createPlasmaBitmap(colors: ArrayList<String>): Bitmap {
    val height = COLOR_BITMAP_SIZE / 4
    val wallpaperBitmap = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888)
    val colorsInt = IntArray(colors.size)
    for (i in colors.indices) {
      colorsInt[i] = Color.parseColor(colors[i])
    }
    val paint = Paint()
    val gradientBitmap = Bitmap.createBitmap(256, 1, Bitmap.Config.ARGB_8888)
    val gradientShader = LinearGradient(0f, 0f, 255f, 0f, colorsInt, null, Shader.TileMode.MIRROR)
    val c = Canvas(gradientBitmap)
    paint.shader = gradientShader
    c.drawRect(0f, 0f, 256f, 1f, paint)
    val palette = IntArray(256)
    for (x in 0..255) {
      palette[x] = gradientBitmap.getPixel(x, 0)
    }
    gradientBitmap.recycle()

    val plasma = Array(height) { IntArray(height) }
    val random = Random()
    val n = 1.3  // Number of periods per wallpaper width.
    val period = height / (n * 2.0 * 3.14)
    val spread = period * 0.3
    val period1 = period - spread + spread * random.nextFloat()
    val period2 = period - spread + spread * random.nextFloat()
    val period3 = period - spread + spread * random.nextFloat()
    for (x in 0 until height)
      for (y in 0 until height) {
        // Adding sines to get plasma value.
        val value = (128.0 + 128.0 * Math.sin(x / period1)
            + 128.0 + 128.0 * Math.sin(y / period2)
            + 128.0 + 128.0 * Math.sin((x + y) / period1)
            + 128.0 + 128.0 * Math.sin(Math.sqrt((x * x + y * y).toDouble()) / period3)).toInt() / 4
        plasma[x][y] = value
      }
    for (x in 0 until height)
      for (y in 0 until height) {
        val color = palette[plasma[x][y] % 256]
        wallpaperBitmap.setPixel(x, y, color)
      }
    return Bitmap.createScaledBitmap(wallpaperBitmap, wallpaperSetter.getDesiredMinimumWidth(),
        wallpaperSetter.getDesiredMinimumHeight(), true)
  }

}