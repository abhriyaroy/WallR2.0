package zebrostudio.wallr100.android.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startForegroundService
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService

class RestartReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    println("broadcast recieved restart wallpaper")
    startForegroundService(context,
        Intent(context, AutomaticWallpaperChangerService::class.java))
  }
}