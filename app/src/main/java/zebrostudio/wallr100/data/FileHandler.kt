package zebrostudio.wallr100.data

import android.os.Environment
import java.io.File

interface FileHandler {

  fun getCacheFile(): File
  fun getModifiedCacheFile(): File
  fun deleteCacheFiles()

}

class FileHandlerImpl : FileHandler {

  private val appDirectoryName = "WallR"
  private val cacheDirectoryName = ".cache"
  private val cacheFolder: File =
      File(Environment.getExternalStorageDirectory().path + File.separator + appDirectoryName
          + File.separator + cacheDirectoryName)
  private val cacheFile: File = File(cacheFolder, System.currentTimeMillis().toString())
  private val modifiedCacheFile: File =
      File(cacheFolder, System.currentTimeMillis().toString())

  override fun getCacheFile(): File {
    createCacheFolder()
    if (!cacheFile.exists()) {
      cacheFile.createNewFile()
    }
    return cacheFile
  }

  override fun getModifiedCacheFile(): File {
    createCacheFolder()
    if (!modifiedCacheFile.exists()) {
      modifiedCacheFile.createNewFile()
    }
    return modifiedCacheFile
  }

  override fun deleteCacheFiles() {
    if (cacheFile.exists()) {
      cacheFile.delete()
    }
    if (modifiedCacheFile.exists())
      modifiedCacheFile.delete()
  }

  private fun createCacheFolder() {
    if (!cacheFolder.exists()) {
      cacheFolder.mkdirs()
    }
  }

}