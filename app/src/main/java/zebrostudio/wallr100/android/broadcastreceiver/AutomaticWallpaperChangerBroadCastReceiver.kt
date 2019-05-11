package zebrostudio.wallr100.android.broadcastreceiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_SERVICE_CODE
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_SERVICE_RESTART_DELAY

class AutomaticWallpaperChangerBroadCastReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    val service = PendingIntent.getService(
        context?.applicationContext,
        WALLPAPER_CHANGER_SERVICE_CODE,
        Intent(context?.applicationContext, AutomaticWallpaperChangerService::class.java),
        PendingIntent.FLAG_ONE_SHOT)

    val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, WALLPAPER_CHANGER_SERVICE_RESTART_DELAY,
        service)
  }
}