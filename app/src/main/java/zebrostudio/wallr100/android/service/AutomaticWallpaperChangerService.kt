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
import io.reactivex.disposables.Disposable
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.NOTIFICATION_CHANNEL_ID
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerUseCase
import javax.inject.Inject

const val WALLPAPER_CHANGER_SERVICE_CODE = 1
const val WALLPAPER_CHANGER_REQUEST_CODE = 2
val wallpaperChangerIntervals = arrayListOf<Long>(
    1800000,
    3600000,
    21600000,
    86400000,
    259200000
)

private const val TIME_CHECKER_DELAY: Long = 300000

class AutomaticWallpaperChangerService : Service() {

  @Inject internal lateinit var serviceManager: ServiceManager
  @Inject internal lateinit var automaticWallpaperChangerUseCase: AutomaticWallpaperChangerUseCase
  @Inject internal lateinit var wallpaperSetter: WallpaperSetter
  @Inject internal lateinit var postExecutionThread: PostExecutionThread

  private var handler: Handler? = null
  private var runnable: Runnable? = null
  private var disposable: Disposable? = null
  private var interval: Long = wallpaperChangerIntervals.first()
  private var timeElapsed: Long = 0

  override fun onCreate() {
    AndroidInjection.inject(this)
    super.onCreate()
  }

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
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

    interval = getInterval()
    handler = Handler()
    runnable = Runnable {
      if (timeElapsed == interval) {
        timeElapsed = 0
        changeWallpaper()
      } else {
        timeElapsed += TIME_CHECKER_DELAY
      }
      handler?.postDelayed(runnable, TIME_CHECKER_DELAY)
    }
    handler?.postDelayed(runnable, TIME_CHECKER_DELAY)

    return START_STICKY
  }

  override fun onDestroy() {
    handler?.removeCallbacks(runnable)
    if (disposable?.isDisposed == false) {
      disposable?.dispose()
    }
    super.onDestroy()
  }

  @Nullable
  override fun onBind(intent: Intent): IBinder? {
    throw IllegalAccessError()
  }

  private fun getIntervalString(interval: Long): String {
    return when (interval) {
      wallpaperChangerIntervals[1] -> stringRes(R.string.wallpaper_changer_service_interval_1_hour)
      wallpaperChangerIntervals[2] -> stringRes(R.string.wallpaper_changer_service_interval_6_hours)
      wallpaperChangerIntervals[3] -> stringRes(R.string.wallpaper_changer_service_interval_1_day)
      wallpaperChangerIntervals[4] -> stringRes(R.string.wallpaper_changer_service_interval_3_days)
      else -> stringRes(R.string.wallpaper_changer_service_interval_30_minutes)
    }
  }

  private fun getInterval(): Long {
    automaticWallpaperChangerUseCase.getInterval().let {
      if (wallpaperChangerIntervals.contains(it)) {
        return it
      } else {
        return wallpaperChangerIntervals.first()
      }
    }
  }

  private fun changeWallpaper() {
    if (disposable?.isDisposed == false) {
      disposable?.dispose()
    }
    disposable = automaticWallpaperChangerUseCase.getWallpaperBitmap()
        .doOnSuccess {
          wallpaperSetter.setWallpaper(it)
          if (!it.isRecycled) {
            it.recycle()
          }
        }.observeOn(postExecutionThread.scheduler)
        .subscribe({
        }, {
          stopSelf()
        })
  }

}