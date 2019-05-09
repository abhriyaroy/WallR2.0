package zebrostudio.wallr100.data

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File

interface FileHandler {
  fun getCacheFile(): File
  fun getCacheFileUriForCropping(): Uri
  fun getDownloadFile(): File
  fun deleteCacheFiles()
  fun getCollectionsFile(): File
  fun getShareableFle(): File
  fun freeSpaceAvailable(): Boolean
  fun ifFileExists(filePath: String): Boolean
  fun deleteFile(filePath: String)
}

const val APP_DIRECTORY_NAME = "WallR"
const val CACHE_DIRECTORY_NAME = ".cache"
const val DOWNLOADS_DIRECTORY_NAME = "Downloads"
const val COLLECTIONS_DIRECTORY_NAME = "Collections"
const val JPG_EXTENSION = ".jpg"
const val MINIMUM_FREE_STORAGE_IN_MB = 20
const val BYTES_TO_MEGA_BYTES = 1048576

class FileHandlerImpl(context: Context) : FileHandler {

  private val cacheFolder: File =
      File(Environment.getExternalStorageDirectory().path + File.separator + APP_DIRECTORY_NAME
          + File.separator + CACHE_DIRECTORY_NAME)
  private val downloadsFolder: File =
      File(Environment.getExternalStorageDirectory().path + File.separator + APP_DIRECTORY_NAME
          + File.separator + DOWNLOADS_DIRECTORY_NAME)
  private val collectionsFolder: File =
      File(Environment.getExternalStorageDirectory().path + File.separator + APP_DIRECTORY_NAME
          + File.separator + COLLECTIONS_DIRECTORY_NAME)
  private val cacheFile: File = File(cacheFolder, System.currentTimeMillis().toString())
  private val cacheCroppedFile: File =
      File(cacheFolder, System.currentTimeMillis().toString())
  private val shareableFile: File =
      File(context.filesDir, System.currentTimeMillis().toString() + JPG_EXTENSION)

  override fun getCacheFile(): File {
    createCacheFolderIfNotPresent()
    if (!cacheFile.exists()) {
      cacheFile.createNewFile()
    }
    return cacheFile
  }

  override fun getCacheFileUriForCropping(): Uri {
    createCacheFolderIfNotPresent()
    if (!cacheCroppedFile.exists()) {
      cacheCroppedFile.createNewFile()
    }
    return Uri.fromFile(cacheCroppedFile)
  }

  override fun getDownloadFile(): File {
    createDownloadsFolderIfNotPresent()
    return File(downloadsFolder, "${System.currentTimeMillis()}$JPG_EXTENSION").apply {
      if (!exists()) {
        createNewFile()
      }
    }
  }

  override fun deleteCacheFiles() {
    if (cacheFolder.exists()) {
      cacheFolder.deleteRecursively()
    }
  }

  override fun getCollectionsFile(): File {
    createCollectionsFolderIfNotPresent()
    return File(collectionsFolder, "${System.currentTimeMillis()}$JPG_EXTENSION").apply {
      if (!exists()) {
        createNewFile()
      }
    }
  }

  override fun getShareableFle(): File {
    return shareableFile
  }

  override fun freeSpaceAvailable(): Boolean {
    val bytesAvailable = Environment.getExternalStorageDirectory().freeSpace
    val megBytesAvailable = bytesAvailable / BYTES_TO_MEGA_BYTES
    return megBytesAvailable > MINIMUM_FREE_STORAGE_IN_MB
  }

  override fun ifFileExists(filePath: String): Boolean {
    if (File(filePath).exists()) {
      return true
    }
    return false
  }

  override fun deleteFile(filePath: String) {
    File(filePath).let {
      if (it.exists()) {
        it.delete()
      }
    }
  }

  private fun createCacheFolderIfNotPresent() {
    if (!cacheFolder.exists()) {
      cacheFolder.mkdirs()
    }
  }

  private fun createDownloadsFolderIfNotPresent() {
    if (!downloadsFolder.exists()) {
      downloadsFolder.mkdirs()
    }
  }

  private fun createCollectionsFolderIfNotPresent() {
    if (!collectionsFolder.exists()) {
      collectionsFolder.mkdirs()
    }
  }

}