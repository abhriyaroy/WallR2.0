package zebrostudio.wallr100.android.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap

interface WallpaperSetter {
  fun setWallpaper(imageBitmap: Bitmap)
}

class WallpaperSetterImpl(private var context: Context) : WallpaperSetter {

  override fun setWallpaper(imageBitmap: Bitmap) {
    val height = WallpaperManager.getInstance(context).desiredMinimumHeight
    val width = WallpaperManager.getInstance(context).desiredMinimumWidth
  }
}