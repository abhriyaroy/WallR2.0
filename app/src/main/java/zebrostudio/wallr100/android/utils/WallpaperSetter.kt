package zebrostudio.wallr100.android.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap

interface WallpaperSetter {
  fun setWallpaper(imageBitmap: Bitmap?): Boolean
}

class WallpaperSetterImpl(private var context: Context) : WallpaperSetter {

  override fun setWallpaper(imageBitmap: Bitmap?): Boolean {
    val wallpaperManager = WallpaperManager.getInstance(context)
    val width = wallpaperManager.desiredMinimumWidth
    val height = wallpaperManager.desiredMinimumHeight
    imageBitmap?.let {
      val scaledBitmapToFitPhoneAspectRatio = Bitmap.createScaledBitmap(it, width, height, false)
      wallpaperManager.setBitmap(scaledBitmapToFitPhoneAspectRatio)
      return true
    }
    return false
  }
}