package zebrostudio.wallr100.android.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService

class BootReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent?) {
    ContextCompat.startForegroundService(context,
        Intent(context, AutomaticWallpaperChangerService::class.java))
  }
}