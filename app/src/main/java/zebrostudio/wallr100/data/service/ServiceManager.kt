package zebrostudio.wallr100.data.service

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startForegroundService

interface ServiceManager {
  fun startAutomaticWallpaperChangerService(): Boolean
  fun stopAutomaticWallpaperChangerService(): Boolean
  fun isAutomaticWallpaperChangerRunning(): Boolean
}

class ServiceManagerImpl(private val context: Context) : ServiceManager {

  override fun startAutomaticWallpaperChangerService(): Boolean {
    Intent(context, AutomaticWallpaperChangerService::class.java).let {
      startForegroundService(context, it)
    }
    if (isAutomaticWallpaperChangerRunning()) {
      return true
    }
    return false
  }

  override fun stopAutomaticWallpaperChangerService(): Boolean {
    Intent(context, AutomaticWallpaperChangerService::class.java).let {
      context.stopService(it)
    }
    if (!isAutomaticWallpaperChangerRunning()) {
      return true
    }
    return false
  }

  override fun isAutomaticWallpaperChangerRunning(): Boolean {
    return isServiceRunningInForeground(AutomaticWallpaperChangerService::class.java)
  }

  private fun isServiceRunningInForeground(serviceClass: Class<*>): Boolean {
    (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).let { manager ->
      for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
          return service.foreground
        }
      }
      println("service false part")
      return false
    }
  }

}