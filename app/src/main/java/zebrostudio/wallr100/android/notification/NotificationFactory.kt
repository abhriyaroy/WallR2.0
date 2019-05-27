package zebrostudio.wallr100.android.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_REQUEST_CODE
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.utils.stringRes

interface NotificationFactory {
  fun getWallpaperChangerNotification(interval: String): Notification
}

const val NOTIFICATION_CHANNEL_ID = "WallrNotificationChannel"
const val NOTIFICATION_CHANNEL_NAME = "WallrAutomaticWallpaperChanger"

class NotificationFactoryImpl(private val context: Context) : NotificationFactory {

  override fun getWallpaperChangerNotification(intervalString: String): Notification {
    createNotificationChannel()
    return createNotification(intervalString)
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val serviceChannel = NotificationChannel(
          NOTIFICATION_CHANNEL_ID,
          NOTIFICATION_CHANNEL_NAME,
          NotificationManager.IMPORTANCE_HIGH
      )
      getSystemService(context, NotificationManager::class.java)!!.createNotificationChannel(
          serviceChannel)
    }
  }

  private fun createNotification(intervalString: String): Notification {
    val pendingIntent = PendingIntent.getActivity(context,
        WALLPAPER_CHANGER_REQUEST_CODE,
        Intent(context, MainActivity::class.java),
        0)
    return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(context.stringRes(R.string.wallpaper_changer_service_notification_title))
        .setContentText("${context.stringRes(
            R.string.wallpaper_changer_service_notification_description)} $intervalString")
        .setSmallIcon(R.drawable.ic_wallr)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setOngoing(true)
        .build()
  }
}