package zebrostudio.wallr100.android.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import dagger.android.AndroidInjection
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.NOTIFICATION_CHANNEL_ID
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import javax.inject.Inject

class AutomaticWallpaperChangerService : Service() {

  @Inject lateinit var automaticWallpaperChangerHelper: AutomaticWallpaperChangerHelper
  @Inject lateinit var postExecutionThread: PostExecutionThread

  private var handler: Handler? = null
  private var runnable : Runnable? = null

  override fun onCreate() {
    AndroidInjection.inject(this)
    super.onCreate()
  }

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    println("Started on star command")
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

    handler = Handler()
    // Define the code block to be executed
    runnable =  object : Runnable {
      override fun run() {
        automaticWallpaperChangerHelper.setWallpaper()
            .observeOn(postExecutionThread.scheduler)
            .subscribe({
              println("Subscribe success ")
              handler?.post(this)
            }, {
              println("subscribe error")
            })
      }
    }
// Start the initial runnable task by posting through the handler
    handler?.post(runnable)

    return START_STICKY
  }

  override fun onDestroy() {
    handler = null
    runnable = null
    super.onDestroy()
  }

  @Nullable
  override fun onBind(intent: Intent): IBinder? {
    return null
  }

}