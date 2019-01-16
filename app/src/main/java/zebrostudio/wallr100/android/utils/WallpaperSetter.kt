package zebrostudio.wallr100.android.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import java.io.IOException

interface WallpaperSetter {
  fun setWallpaper(imageBitmap: Bitmap?): Boolean
}

class WallpaperSetterImpl(private var context: Context) : WallpaperSetter {

  override fun setWallpaper(imageBitmap: Bitmap?): Boolean {
    val wallpaperManager = WallpaperManager.getInstance(context)
    val height = wallpaperManager.desiredMinimumHeight
    val width = wallpaperManager.desiredMinimumWidth
    return try {
      if (imageBitmap != null) {
        val resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false)
        wallpaperManager.setBitmap(resizedBitmap)
        true
      } else {
        false
      }
    } catch (e: IOException) {
      false
    }
  }
}