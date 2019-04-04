package zebrostudio.wallr100.android.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build

interface WallpaperSetter {
  fun setWallpaper(imageBitmap: Bitmap?): Boolean
  fun getDesiredMinimumWidth(): Int
  fun getDesiredMinimumHeight(): Int
}

const val SDK_VERSION_24 = 24

class WallpaperSetterImpl(private var context: Context) : WallpaperSetter {

  private var wallpaperManager: WallpaperManager? = null

  override fun setWallpaper(imageBitmap: Bitmap?): Boolean {
    imageBitmap?.let {
      val scaledBitmapToFitPhoneAspectRatio =
          Bitmap.createScaledBitmap(it, getDesiredMinimumWidth(), getDesiredMinimumHeight(), false)
      getWallpaperManagerInstance().setBitmap(scaledBitmapToFitPhoneAspectRatio)
      if (Build.VERSION.SDK_INT >= SDK_VERSION_24) {
        getWallpaperManagerInstance().setBitmap(scaledBitmapToFitPhoneAspectRatio, null,
            true, WallpaperManager.FLAG_LOCK)
      }
      scaledBitmapToFitPhoneAspectRatio.recycle()
      return true
    }
    return false
  }

  override fun getDesiredMinimumWidth(): Int {
    return getWallpaperManagerInstance().desiredMinimumWidth
  }

  override fun getDesiredMinimumHeight(): Int {
    return getWallpaperManagerInstance().desiredMinimumHeight
  }

  private fun getWallpaperManagerInstance(): WallpaperManager {
    if (wallpaperManager == null) {
      wallpaperManager = WallpaperManager.getInstance(context)
    }
    return wallpaperManager!!
  }
}