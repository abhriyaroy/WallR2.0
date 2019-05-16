package zebrostudio.wallr100.android.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat.startForegroundService
import android.widget.Toast
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService

class BootReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent?) {
    Toast.makeText(context, "Boot received here", Toast.LENGTH_LONG).show()
    startForegroundService(context,
        Intent(context, AutomaticWallpaperChangerService::class.java))
  }
}