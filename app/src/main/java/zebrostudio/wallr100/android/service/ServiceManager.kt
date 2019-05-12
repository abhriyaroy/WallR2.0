package zebrostudio.wallr100.android.service

import android.app.ActivityManager
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startForegroundService
import android.support.v4.content.ContextCompat.getSystemService



interface ServiceManager {
  fun startAutomaticWallpaperChangerService(): Boolean
  fun stopAutomaticWallpaperChangerService(): Boolean
  fun isAutomaticWallpaperChangerRunning(): Boolean
}

class ServiceManagerImpl(private val context: Context) : ServiceManager {

  private var alarmManager: AlarmManager? = null
  private var pendingIntent: PendingIntent? = null

  override fun startAutomaticWallpaperChangerService(): Boolean {
    if (!isAutomaticWallpaperChangerRunning()) {
      startForegroundService(context, Intent(context, AutomaticWallpaperChangerService::class.java))
      /*alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      val intent = Intent(context, AutomaticWallpaperChangerService::class.java)
      pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)*/
    }
    if (isAutomaticWallpaperChangerRunning()) {
      return true
    }
    return false
  }

  override fun stopAutomaticWallpaperChangerService(): Boolean {
    //alarmManager?.cancel(pendingIntent)
    Intent(context, AutomaticWallpaperChangerService::class.java).let {
      context.stopService(it)
    }
    if (!isAutomaticWallpaperChangerRunning()) {
      return true
    }
    return false
  }

  override fun isAutomaticWallpaperChangerRunning(): Boolean {
    return isServiceRunningInForeground(
        AutomaticWallpaperChangerService::class.java)
  }

  private fun isServiceRunningInForeground(serviceClass: Class<*>): Boolean {
    (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).let { manager ->
      for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
          return service.foreground
        }
      }
      return false
    }
  }

}