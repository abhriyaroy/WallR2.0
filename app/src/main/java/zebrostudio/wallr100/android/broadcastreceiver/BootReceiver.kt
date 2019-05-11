package zebrostudio.wallr100.android.broadcastreceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_SERVICE_CODE
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_SERVICE_RESTART_DELAY

class BootReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent?) {

    val service = PendingIntent.getService(
        context.applicationContext,
        WALLPAPER_CHANGER_SERVICE_CODE,
        Intent(context.applicationContext, AutomaticWallpaperChangerService::class.java),
        PendingIntent.FLAG_ONE_SHOT)

    ContextCompat.startForegroundService(context,
        Intent(context, AutomaticWallpaperChangerService::class.java))
  }
}