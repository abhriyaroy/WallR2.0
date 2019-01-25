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
    if (wallpaperManager == null) {
      wallpaperManager = WallpaperManager.getInstance(context)
    }
    imageBitmap?.let {
      val scaledBitmapToFitPhoneAspectRatio =
          Bitmap.createScaledBitmap(it, getDesiredMinimumWidth(), getDesiredMinimumHeight(), false)
      wallpaperManager!!.setBitmap(scaledBitmapToFitPhoneAspectRatio)
      scaledBitmapToFitPhoneAspectRatio.recycle()
      return true
    }
    return false
  }

  override fun getDesiredMinimumWidth(): Int {
    if (wallpaperManager == null) {
      wallpaperManager = WallpaperManager.getInstance(context)
    }
    return wallpaperManager!!.desiredMinimumWidth
  }

  override fun getDesiredMinimumHeight(): Int {
    if (wallpaperManager == null) {
      wallpaperManager = WallpaperManager.getInstance(context)
    }
    return wallpaperManager!!.desiredMinimumHeight
  }
}