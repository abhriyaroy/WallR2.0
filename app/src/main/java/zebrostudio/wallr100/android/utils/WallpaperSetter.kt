package zebrostudio.wallr100.android.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap

interface WallpaperSetter {
  fun setWallpaper(imageBitmap: Bitmap?): Boolean
  fun getDesiredMinimumWidth(): Int
  fun getDesiredMinimumHeight(): Int
}

class WallpaperSetterImpl(private var context: Context) : WallpaperSetter {

  private var wallpaperManager: WallpaperManager? = null

  override fun setWallpaper(imageBitmap: Bitmap?): Boolean {
    imageBitmap?.let {
      val scaledBitmapToFitPhoneAspectRatio =
          Bitmap.createScaledBitmap(it, getDesiredMinimumWidth(), getDesiredMinimumHeight(), false)
      getWallpaperManagerInstance().setBitmap(scaledBitmapToFitPhoneAspectRatio)
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