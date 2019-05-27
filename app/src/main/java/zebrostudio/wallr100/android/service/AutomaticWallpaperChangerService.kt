package zebrostudio.wallr100.android.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
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
const val ILLEGAL_ACCESS_ERROR_MESSAGE = "Wallpaper changer service cannot be bounded to"
val WALLPAPER_CHANGER_INTERVALS_LIST = listOf<Long>(
    1800000,
    3600000,
    21600000,
    86400000,
    259200000
)

class AutomaticWallpaperChangerServiceImpl : Service(), AutomaticWallpaperChangerService {

  @Inject
  internal lateinit var automaticWallpaperChangerUseCase: AutomaticWallpaperChangerUseCase

  override fun onCreate() {
    AndroidInjection.inject(this)
    super.onCreate()
    automaticWallpaperChangerUseCase.attachService(this)
  }

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    createNotification()
    automaticWallpaperChangerUseCase.handleServiceCreated()
    return START_NOT_STICKY
  }

  override fun onDestroy() {
    automaticWallpaperChangerUseCase.detachService()
    super.onDestroy()
  }

  @Nullable
  override fun onBind(intent: Intent): IBinder? {
    throw IllegalAccessError(ILLEGAL_ACCESS_ERROR_MESSAGE)
  }

  override fun stopService() {
    stopService()
  }

  private fun createNotification() {
    val pendingIntent = PendingIntent.getActivity(this,
        WALLPAPER_CHANGER_REQUEST_CODE,
        Intent(this, MainActivity::class.java),
        0)
    val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(stringRes(R.string.wallpaper_changer_service_notification_title))
        .setContentText("${stringRes(
            R.string.wallpaper_changer_service_notification_description)} ${automaticWallpaperChangerUseCase.getIntervalString()}")
        .setSmallIcon(R.drawable.ic_wallr)
        .setContentIntent(pendingIntent)
        .setPriority(PRIORITY_MAX)
        .setOngoing(true)
        .build()
    startForeground(WALLPAPER_CHANGER_SERVICE_CODE, notification)
  }
}