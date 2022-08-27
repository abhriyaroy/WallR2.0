package zebrostudio.wallr100.data

import android.content.Context
import android.graphics.*
import android.graphics.Bitmap.CompressFormat.JPEG
import android.graphics.Paint.Style.FILL
import android.graphics.Shader.TileMode.CLAMP
import android.graphics.Shader.TileMode.MIRROR
import android.net.Uri
import com.zebrostudio.lowpolyrxjava.LowPolyRx
import io.reactivex.Completable
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
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

interface ImageHandler {
  fun isImageCached(link: String): Boolean
  fun fetchImage(link: String): Observable<Long>
  fun cancelFetchingImage()
  fun getImageBitmap(): Single<Bitmap>
  fun getImageUri(): Single<Uri>
  fun clearImageCache(): Completable
  fun getShareableUri(): Single<Uri>
  fun convertUriToBitmap(uri: Uri?): Single<Bitmap>
  fun convertImageInCacheToLowpoly(): Single<Bitmap>
  fun addCachedImageToDownloads(): Completable
  fun addImageToCollections(data: String, databaseImageType: DatabaseImageType): Completable
  fun getSingleColorBitmap(hexValue: String): Single<Bitmap>
  fun getMultiColorBitmap(
    hexValueList: List<String>,
    multiColorImageType: MultiColorImageType
  ): Single<Bitmap>

  fun getAllImagesInCollection(): Single<List<CollectionDatabaseImageEntity>>
  fun addExternalImageToCollection(uriList: List<Uri>): Completable
  fun reorderImagesInCollection(
    collectionDatabaseImageEntityList: List<CollectionDatabaseImageEntity>
  ): Single<List<CollectionDatabaseImageEntity>>

  fun deleteImagesInCollection(
    collectionDatabaseImageEntityList: List<CollectionDatabaseImageEntity>
  ): Single<List<CollectionDatabaseImageEntity>>

  fun getImageBitmap(path: String): Bitmap
  fun convertAndCacheLowpolyImage(path: String, databaseImageType: DatabaseImageType): Completable
}

const val BYTE_ARRAY_SIZE = 2048
const val DOWNLOAD_PROGRESS_COMPLETED_VALUE: Long = 100
const val READ_MODE = "r"
const val BITMAP_COMPRESS_QUALITY = 100
const val INITIAL_SIZE = 0
const val UID_AUTO_INCREMENT: Long = 0
const val COLOR_BITMAP_SIZE: Int = 512
private const val END_OF_BYTE_STREAM: Int = -1

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

  override fun getImageBitmap(): Single<Bitmap> {
    return Single.create { emitter ->
      BitmapFactory.Options().let {
        it.inPreferredConfig = Bitmap.Config.ARGB_8888
        emitter.onSuccess(BitmapFactory.decodeFile(fileHandler.getCacheFile().path, it))
      }
    }
  }

  override fun clearImageCache(): Completable {
    return Completable.create {
      fileHandler.deleteCacheFiles()
      imageCacheTracker = Pair(false, "")
      it.onComplete()
    }
  }

  override fun getImageUri(): Single<Uri> {
    return getImageBitmap()
        .map { bitmap ->
          fileHandler.getCacheFile().outputStream()
              .compressBitmap(bitmap, JPEG, BITMAP_COMPRESS_QUALITY)
          Uri.fromFile(fileHandler.getCacheFile())
        }
  }

  override fun getShareableUri(): Single<Uri> {
    return getImageBitmap()
        .map { bitmap ->
          fileHandler.getShareableFile().outputStream()
              .compressBitmap(bitmap, JPEG, BITMAP_COMPRESS_QUALITY)
          fileHandler.getShareableUri()
        }
  }

  override fun convertUriToBitmap(uri: Uri?): Single<Bitmap> {
    return Single.create {
      if (uri == null) {
        it.onError(NullPointerException())
      } else {
        try {
          with(context.contentResolver.openFileDescriptor(uri, READ_MODE)) {
            this?.fileDescriptor?.let { fileDescriptor->
              BitmapFactory.decodeFileDescriptor(fileDescriptor).let { bitmap ->
                this.close()
                fileHandler.getCacheFile().outputStream()
                  .compressBitmap(bitmap, JPEG, BITMAP_COMPRESS_QUALITY)
                it.onSuccess(bitmap)
              }
            }
          }
        } catch (exception: IOException) {
          it.onError(exception)
        }
      }
    }
  }

  override fun convertImageInCacheToLowpoly(): Single<Bitmap> {
    return getImageBitmap()
        .flatMap {
          LowPolyRx().getLowPolyImage(it)
        }.map {
          fileHandler.getCacheFile().outputStream()
              .compressBitmap(it, JPEG, BITMAP_COMPRESS_QUALITY)
          it
        }
  }

  override fun addCachedImageToDownloads(): Completable {
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

  override fun addImageToCollections(
    data: String,
    databaseImageType: DatabaseImageType
  ): Completable {
    return if (databaseImageType == EDITED) {
      saveToCollection(data, databaseImageType)
    } else {
      checkIfImageIsAlreadyPresent(data, databaseImageType)
          .andThen(saveToCollection(data, databaseImageType))
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

  override fun getAllImagesInCollection(): Single<List<CollectionDatabaseImageEntity>> {
    return databaseHelper.getDatabase().collectionsDao().getAllData()
        .flatMap { originalList ->
          mutableListOf<CollectionDatabaseImageEntity>().let { newList ->
            originalList.forEach { collectionDatabaseImageEntity ->
              if (fileHandler.fileExists(collectionDatabaseImageEntity.path)) {
                newList.add(collectionDatabaseImageEntity)
              } else {
                databaseHelper.getDatabase().collectionsDao()
                    .deleteData(collectionDatabaseImageEntity)
              }
            }
            Single.just(newList)
          }
        }
  }

  override fun addExternalImageToCollection(uriList: List<Uri>): Completable {
    return Completable.create {
      try {
        uriList.forEach { uri ->
          saveFileToCollections(uri).let { outputFile ->
            databaseHelper.getDatabase().collectionsDao().insert(CollectionDatabaseImageEntity(
              UID_AUTO_INCREMENT,
              outputFile.name,
              outputFile.path,
              uri.toString(),
              DatabaseImageType.EXTERNAL.ordinal
            ))
          }
        }
        it.onComplete()
      } catch (ioException: IOException) {
        it.onError(ioException)
      }
    }
  }

  override fun reorderImagesInCollection(
    collectionDatabaseImageEntityList: List<CollectionDatabaseImageEntity>
  ): Single<List<CollectionDatabaseImageEntity>> {
    return makeNewCollection(collectionDatabaseImageEntityList)
        .andThen(databaseHelper.getDatabase().collectionsDao().getAllData())
  }

  override fun deleteImagesInCollection(
    collectionDatabaseImageEntityList: List<CollectionDatabaseImageEntity>
  ): Single<List<CollectionDatabaseImageEntity>> {
    return removeImagesFromCollection(collectionDatabaseImageEntityList)
        .andThen(databaseHelper.getDatabase().collectionsDao().getAllData())
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

  override fun getImageBitmap(path: String): Bitmap {
    return BitmapFactory.Options().let {
      it.inPreferredConfig = Bitmap.Config.ARGB_8888
      BitmapFactory.decodeFile(path, it)
    }
  }

  override fun convertAndCacheLowpolyImage(
    path: String,
    databaseImageType: DatabaseImageType
  ): Completable {
    return checkIfImageIsAlreadyPresent(path, databaseImageType)
        .andThen(generateLowpolyImage(path))
  }

  private fun checkIfImageIsAlreadyPresent(
    data: String,
    type: DatabaseImageType
  ): Completable {
    return databaseHelper.getDatabase().collectionsDao().getAllData()
        .flatMapCompletable { it ->
          var isPresent = false
          it.forEach {
            if (it.type == type.ordinal && it.data == data) {
              isPresent = true
            }
          }
          if (isPresent) {
            Completable.error(AlreadyPresentInCollectionException())
          } else {
            Completable.complete()
          }
        }
  }

  private fun removeImagesFromCollection(
    collectionDatabaseImageEntityList: List<CollectionDatabaseImageEntity>
  ): Completable {
    return Completable.create {
      collectionDatabaseImageEntityList.forEach {
        databaseHelper.getDatabase().collectionsDao().deleteData(it)
        fileHandler.deleteFile(it.path)
      }
      it.onComplete()
    }
  }

  private fun saveToCollection(
    data: String,
    type: DatabaseImageType
  ): Completable {
    return Completable.create {
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
        it.onComplete()
      }
    }
  }

  private fun saveFileToCollections(sourceUri: Uri): File {
    fileHandler.getCollectionsFile().let { collectionsFile ->
      context.contentResolver.openInputStream(sourceUri)!!.let { inputStream ->
        BufferedOutputStream(FileOutputStream(collectionsFile, false)).let { outputStream ->
          val byteArray = ByteArray(BYTE_ARRAY_SIZE)
          inputStream.read(byteArray)
          do {
            outputStream.write(byteArray)
          } while (inputStream.read(byteArray) != END_OF_BYTE_STREAM)
          outputStream.close()
        }
        inputStream.close()
      }
      return collectionsFile
    }
  }

  private fun makeNewCollection(collectionDatabaseImageEntityList: List<CollectionDatabaseImageEntity>)
      : Completable {
    return Completable.create {
      databaseHelper.getDatabase().collectionsDao().let { collectionsDao ->
        collectionsDao.deleteAllData()
        collectionDatabaseImageEntityList.forEach {
          collectionsDao.insert(
            CollectionDatabaseImageEntity(
              UID_AUTO_INCREMENT,
              it.name,
              it.path,
              it.data,
              it.type
            )
          )
        }
      }
      it.onComplete()
    }
  }

  private fun generateLowpolyImage(path: String): Completable {
    return Completable.create { emitter ->
      getImageBitmap(path).let {
        LowPolyRx().getLowPolyImage(it).blockingGet().let { bitmap ->
          try {
            fileHandler.getCacheFile().outputStream()
                .compressBitmap(bitmap, JPEG, BITMAP_COMPRESS_QUALITY)
            emitter.onComplete()
          } catch (exception: IOException) {
            emitter.onError(exception)
          }
        }
      }
    }
  }

  private fun createMaterialBitmap(colors: ArrayList<String>): Bitmap {
    val smallHeight = wallpaperSetter.getDesiredMinimumHeight()
    val bigHeight = 2 * smallHeight
    val middleHeight = (bigHeight / Math.sqrt(2.0)).toInt()
    val offset = (bigHeight - middleHeight) / 2
    val bigBitmap = Bitmap.createBitmap(bigHeight, bigHeight, Bitmap.Config.ARGB_8888)
    val colorsInt = IntArray(colors.size)
    for (i in colors.indices) {
      colorsInt[i] = Color.parseColor(colors[i])
    }
    val canvas = Canvas(bigBitmap)
    canvas.save()
    canvas.rotate(-45f, (canvas.width / 2).toFloat(), (canvas.height / 2).toFloat())
    val paint = Paint()
    val initStripeHeight = (middleHeight / colors.size).toFloat()
    val initShadowHeight = (middleHeight * 0.012).toFloat()
    val stripeSpread = (initStripeHeight * 0.25).toInt()
    val shadowSpread = initShadowHeight * 0.5f
    for (i in colors.indices.reversed()) {
      var stripeHeight: Int
      val shadowThickness: Float
      if (i == colors.size - 1) {
        stripeHeight = bigHeight
        shadowThickness = 0f
      } else {
        stripeHeight = Math.round((i + 1) * initStripeHeight)
        val variableColorSpread = (stripeSpread * Math.random() - stripeSpread / 2).toInt()
        stripeHeight += offset + variableColorSpread
        if (stripeHeight < 0) stripeHeight = 0
        if (stripeHeight > bigHeight) stripeHeight = bigHeight
        val variableShadowSpread = (shadowSpread * Math.random() - shadowSpread / 2).toFloat()
        shadowThickness = Math.max(1f, initShadowHeight + variableShadowSpread)
      }
      paint.color = colorsInt[i]
      paint.style = FILL
      paint.setShadowLayer(shadowThickness, 0.0f, 0.0f, -0x1000000)
      canvas.drawRect(0f, 0f, bigHeight.toFloat(), stripeHeight.toFloat(), paint)
    }
    canvas.restore()
    val startingXCoordinate = (canvas.width - smallHeight) / 2
    val startingYCoordinate = (canvas.height - smallHeight) / 2
    return Bitmap.createBitmap(bigBitmap, startingXCoordinate, startingYCoordinate, smallHeight,
      smallHeight)
  }

  private fun createGradientBitmap(colors: ArrayList<String>): Bitmap {
    val height = COLOR_BITMAP_SIZE
    val wallpaperBitmap = Bitmap.createBitmap(height, height, Bitmap.Config.ARGB_8888)
    val colorsInt = IntArray(colors.size)
    for (i in colors.indices) {
      colorsInt[i] = Color.parseColor(colors[i])
    }
    val paint = Paint()
    val gradientShader = LinearGradient(0f, 0f, height.toFloat(), height.toFloat(), colorsInt,
      null, CLAMP)
    val canvas = Canvas(wallpaperBitmap)
    paint.shader = gradientShader
    canvas.drawRect(0f, 0f, height.toFloat(), height.toFloat(), paint)
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
    val gradientShader = LinearGradient(0f, 0f, 255f, 0f, colorsInt, null,
      MIRROR)
    val canvas = Canvas(gradientBitmap)
    paint.shader = gradientShader
    canvas.drawRect(0f, 0f, 256f, 1f, paint)
    val palette = IntArray(256)
    for (x in 0..255) {
      palette[x] = gradientBitmap.getPixel(x, 0)
    }
    gradientBitmap.recycle()

    val plasma = Array(height) { IntArray(height) }
    val random = Random()
    val period = height / (1.3 * 2.0 * 3.14)
    val spread = period * 0.3
    val period1 = period - spread + spread * random.nextFloat()
    val period2 = period - spread + spread * random.nextFloat()
    val period3 = period - spread + spread * random.nextFloat()
    for (x in 0 until height)
      for (y in 0 until height) {
        val value = (128.0 + 128.0 * Math.sin(x / period1)
            + 128.0 + 128.0 * Math.sin(y / period2)
            + 128.0 + 128.0 * Math.sin((x + y) / period1)
            + 128.0 + 128.0 * Math.sin(
          Math.sqrt((x * x + y * y).toDouble()) / period3)).toInt() / 4
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