package zebrostudio.wallr100.android.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
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
import zebrostudio.wallr100.android.ui.collection.REQUEST_CODE
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.withDelayOnMain
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerUseCase
import javax.inject.Inject

const val WALLPAPER_CHANGER_SERVICE_RESTART_DELAY: Long = 2000
const val WALLPAPER_CHANGER_SERVICE_CODE = 1
val wallpaperChangerIntervals = arrayListOf<Long>(
    1800000,
    3600000,
    21600000,
    86400000,
    259200000
)

private const val TIME_CHECKER_DELAY: Long = 300000

class AutomaticWallpaperChangerService : Service() {

  @Inject lateinit var serviceManager: ServiceManager
  @Inject lateinit var automaticWallpaperChangerUseCase: AutomaticWallpaperChangerUseCase
  @Inject lateinit var wallpaperSetter: WallpaperSetter
  @Inject lateinit var postExecutionThread: PostExecutionThread

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
    println("Started on star command")
    val notificationIntent = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this,
        REQUEST_CODE, notificationIntent, 0)

    val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setContentTitle(stringRes(R.string.wallpaper_changer_service_notification_title))
        .setContentText("${stringRes(
            R.string.wallpaper_changer_service_notification_description)} ${getIntervalString(
            automaticWallpaperChangerUseCase.getInterval())}")
        .setSmallIcon(R.drawable.ic_wallr)
        .setContentIntent(pendingIntent)
        .setOngoing(true)
        .setPriority(PRIORITY_MAX)
        .build()

    startForeground(WALLPAPER_CHANGER_SERVICE_CODE, notification)

    interval = getInterval()

    handler = Handler()
    runnable = Runnable {
      println("Runnable fired $timeElapsed")
      if (timeElapsed == interval) {
        timeElapsed = 0
        changeWallpaper()
      } else {
        timeElapsed += TIME_CHECKER_DELAY
      }
      handler?.postDelayed(runnable, TIME_CHECKER_DELAY)
    }
    handler?.postDelayed(runnable, TIME_CHECKER_DELAY)

    /*val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

    val currentTime = System.currentTimeMillis()
    alarmManager.setRepeating(
        AlarmManager.RTC_WAKEUP,
        currentTime + oneMinute,
        oneMinute,
        pendingIntent)*/

    //changeWallpaper()

    return START_STICKY
  }

  override fun onDestroy() {
    handler?.removeCallbacks(runnable)
    println("service wallapper changer destroyed")
    if (disposable?.isDisposed == false) {
      disposable?.dispose()
    }
    super.onDestroy()
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    /*withDelayOnMain(WALLPAPER_CHANGER_SERVICE_RESTART_DELAY) {
      serviceManager.startAutomaticWallpaperChangerService()
    }*/

    super.onTaskRemoved(rootIntent)
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
    println("subscribe run")
    println("thread is ${Thread.currentThread().name}")
    if (disposable?.isDisposed == false) {
      disposable?.dispose()
    }
    disposable = automaticWallpaperChangerUseCase.getWallpaperBitmap()
        .doOnSuccess {
          println("on success")
          wallpaperSetter.setWallpaper(it)
          if (!it.isRecycled) {
            it.recycle()
          }
        }.observeOn(postExecutionThread.scheduler)
        .subscribe({
          println("Subscribe success ")
        }, {
          println(it.message)
          println("subscribe error")
          stopSelf()
        })
  }

}