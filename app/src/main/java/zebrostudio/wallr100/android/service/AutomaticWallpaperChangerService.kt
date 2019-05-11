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
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerUseCase
import javax.inject.Inject

const val WALLPAPER_CHANGER_SERVICE_RESTART_DELAY: Long = 1000
const val WALLPAPER_CHANGER_SERVICE_CODE = 1

val wallpaperChangerIntervals = arrayListOf<Long>(
    1800000,
    3600000,
    21600000,
    86400000,
    259200000
)

class AutomaticWallpaperChangerService : Service() {

  @Inject lateinit var automaticWallpaperChangerUseCase: AutomaticWallpaperChangerUseCase
  @Inject lateinit var wallpaperSetter: WallpaperSetter
  @Inject lateinit var postExecutionThread: PostExecutionThread

  private var handler: Handler? = null
  private var runnable: Runnable? = null
  private var disposable: Disposable? = null

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
        .setContentTitle(stringRes(R.string.wallpaper_changer_service_notification_title))
        .setContentText("${stringRes(
            R.string.wallpaper_changer_service_notification_description)} ${getInterval(
            automaticWallpaperChangerUseCase.getInterval())}")
        .setSmallIcon(R.drawable.ic_wallr)
        .setContentIntent(pendingIntent)
        .setOngoing(true)
        .setPriority(PRIORITY_MAX)
        .build()

    startForeground(WALLPAPER_CHANGER_SERVICE_CODE, notification)

    handler = Handler()
    runnable = object : Runnable {
      override fun run() {
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
              handler?.postDelayed(this, automaticWallpaperChangerUseCase.getInterval())
            }, {
              println(it.message)
              println("subscribe error")
              stopSelf()
            })
      }
    }
    handler?.post(runnable)

    return START_REDELIVER_INTENT
  }

  override fun onDestroy() {
    handler = null
    runnable = null
    println("service wallapper changer destroyed")
    if (disposable?.isDisposed == false) {
      disposable?.dispose()
    }
    super.onDestroy()
  }

  override fun onTaskRemoved(rootIntent: Intent?) {
    super.onTaskRemoved(rootIntent)

    val service = PendingIntent.getService(
        applicationContext,
        WALLPAPER_CHANGER_SERVICE_CODE,
        Intent(applicationContext, AutomaticWallpaperChangerService::class.java),
        PendingIntent.FLAG_ONE_SHOT)

    val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, WALLPAPER_CHANGER_SERVICE_RESTART_DELAY,
        service)
  }

  @Nullable
  override fun onBind(intent: Intent): IBinder? {
    throw IllegalAccessError()
  }

  private fun getInterval(interval: Long): String {
    return when (interval) {
      wallpaperChangerIntervals[0] -> stringRes(
          R.string.wallpaper_changer_service_interval_30_minutes)
      wallpaperChangerIntervals[1] -> stringRes(R.string.wallpaper_changer_service_interval_1_hour)
      wallpaperChangerIntervals[2] -> stringRes(R.string.wallpaper_changer_service_interval_6_hours)
      wallpaperChangerIntervals[3] -> stringRes(R.string.wallpaper_changer_service_interval_1_day)
      else -> stringRes(R.string.wallpaper_changer_service_interval_3_days)
    }
  }

}