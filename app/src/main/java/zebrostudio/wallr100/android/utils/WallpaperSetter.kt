package zebrostudio.wallr100.android.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import org.jetbrains.anko.doAsync

interface WallpaperSetter {
  fun setWallpaper(imageBitmap: Bitmap?): Boolean
}

class WallpaperSetterImpl(private var context: Context) : WallpaperSetter {

  override fun setWallpaper(imageBitmap: Bitmap?): Boolean {
    val wallpaperManager = WallpaperManager.getInstance(context)
    val width = wallpaperManager.desiredMinimumWidth
    val height = wallpaperManager.desiredMinimumHeight
    if (imageBitmap != null) {
      doAsync {
        val scaledBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        wallpaperManager.setBitmap(scaledBitmap)
      }
      return true
    }
    return false
  }
}