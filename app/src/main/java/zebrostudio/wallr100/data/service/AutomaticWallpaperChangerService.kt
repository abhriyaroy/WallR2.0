package zebrostudio.wallr100.data.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.NOTIFICATION_CHANNEL_ID
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.domain.executor.PostExecutionThread

class AutomaticWallpaperChangerService(
  private val automaticWallpaperChangerHelper: AutomaticWallpaperChangerHelper,
    private val postExecutionThread: PostExecutionThread
) : Service() {

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

    val notificationIntent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this,
        0, notificationIntent, 0)

    val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(stringRes(R.string.automatic_wallpaper_changer_notification_title))
        .setContentText("Changing wallpaper every")
        .setSmallIcon(R.drawable.ic_wallr)
        .setContentIntent(pendingIntent)
        .build()

    startForeground(1, notification)

    //do heavy work on a background thread
    //stopSelf();

    val handler = Handler()
    // Define the code block to be executed
    val runnableCode = object : Runnable {
      override fun run() {
        automaticWallpaperChangerHelper.setWallpaper()
            .observeOn(postExecutionThread.scheduler)
            .subscribe( {
              println("Subscribe success ")
              handler.postDelayed(this, 2000)
            },{
              println("subscribe error")
            })
      }
    }
// Start the initial runnable task by posting through the handler
    handler.post(runnableCode)

    return START_STICKY
  }

  @Nullable
  override fun onBind(intent: Intent): IBinder? {
    return null
  }

}