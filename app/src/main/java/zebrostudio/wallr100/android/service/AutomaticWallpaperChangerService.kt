package zebrostudio.wallr100.android.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.PRIORITY_MAX
import dagger.android.AndroidInjection
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.NOTIFICATION_CHANNEL_ID
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerUseCase
import javax.inject.Inject

interface AutomaticWallpaperChangerService {
  fun stopService()
}

const val WALLPAPER_CHANGER_SERVICE_CODE = 1
const val WALLPAPER_CHANGER_REQUEST_CODE = 2
val WALLPAPER_CHANGER_INTERVALS_LIST = listOf<Long>(
    1800000,
    3600000,
    21600000,
    86400000,
    259200000
)

private const val TIME_CHECKER_DELAY: Long = 300000

class AutomaticWallpaperChangerServiceImpl : Service(),
    AutomaticWallpaperChangerService {

  @Inject internal lateinit var automaticWallpaperChangerUseCase: AutomaticWallpaperChangerUseCase

  private var handler: Handler? = null
  private var runnable: Runnable? = null

  override fun onCreate() {
    AndroidInjection.inject(this)
    super.onCreate()
  }

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    createNotification()
    automaticWallpaperChangerUseCase.attachService(this)
    handler = Handler()
    runnable = Runnable {
      automaticWallpaperChangerUseCase.handleRunnableCall()
      handler?.postDelayed(runnable, TIME_CHECKER_DELAY)
    }
    handler?.postDelayed(runnable, TIME_CHECKER_DELAY)
    return START_STICKY
  }

  override fun onDestroy() {
    handler?.removeCallbacks(runnable)
    handler = null
    runnable = null
    automaticWallpaperChangerUseCase.detachService()
    super.onDestroy()
  }

  @Nullable
  override fun onBind(intent: Intent): IBinder? {
    throw IllegalAccessError()
  }

  override fun stopService() {
    stopService()
  }

  private fun createNotification() {
    val notificationIntent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this,
        WALLPAPER_CHANGER_REQUEST_CODE, notificationIntent, 0)
    val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(stringRes(R.string.wallpaper_changer_service_notification_title))
        .setContentText("${stringRes(
            R.string.wallpaper_changer_service_notification_description)} ${getIntervalString(
            automaticWallpaperChangerUseCase.getInterval())}")
        .setSmallIcon(R.drawable.ic_wallr)
        .setContentIntent(pendingIntent)
        .setAutoCancel(false)
        .setOngoing(true)
        .setPriority(PRIORITY_MAX)
        .build()
    startForeground(WALLPAPER_CHANGER_SERVICE_CODE, notification)
  }

  private fun getIntervalString(interval: Long): String {
    return when (interval) {
      WALLPAPER_CHANGER_INTERVALS_LIST[1] -> stringRes(R.string.wallpaper_changer_service_interval_1_hour)
      WALLPAPER_CHANGER_INTERVALS_LIST[2] -> stringRes(R.string.wallpaper_changer_service_interval_6_hours)
      WALLPAPER_CHANGER_INTERVALS_LIST[3] -> stringRes(R.string.wallpaper_changer_service_interval_1_day)
      WALLPAPER_CHANGER_INTERVALS_LIST[4] -> stringRes(R.string.wallpaper_changer_service_interval_3_days)
      else -> stringRes(R.string.wallpaper_changer_service_interval_30_minutes)
    }
  }

}