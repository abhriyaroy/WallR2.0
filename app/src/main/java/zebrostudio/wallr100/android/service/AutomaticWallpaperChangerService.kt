package zebrostudio.wallr100.android.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.Nullable
import dagger.android.AndroidInjection
import zebrostudio.wallr100.android.notification.NotificationFactory
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerUseCase
import java.util.concurrent.TimeUnit
import javax.inject.Inject

interface AutomaticWallpaperChangerService {
  fun stopService()
}

const val WALLPAPER_CHANGER_SERVICE_CODE = 1
const val WALLPAPER_CHANGER_REQUEST_CODE = 2
const val ILLEGAL_ACCESS_ERROR_MESSAGE = "Wallpaper changer service cannot be bounded to"
val WALLPAPER_CHANGER_INTERVALS_LIST = listOf<Long>(
  TimeUnit.MINUTES.toMillis(30),
  TimeUnit.HOURS.toMillis(1),
  TimeUnit.HOURS.toMillis(6),
  TimeUnit.DAYS.toMillis(1),
  TimeUnit.DAYS.toMillis(3)
)

class AutomaticWallpaperChangerServiceImpl : Service(), AutomaticWallpaperChangerService {

  @Inject
  internal lateinit var automaticWallpaperChangerUseCase: AutomaticWallpaperChangerUseCase
  @Inject
  internal lateinit var notificationFactory: NotificationFactory

  override fun onCreate() {
    AndroidInjection.inject(this)
    super.onCreate()
    automaticWallpaperChangerUseCase.attachService(this)
  }

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    startForeground(WALLPAPER_CHANGER_SERVICE_CODE,
      notificationFactory.getWallpaperChangerNotification(
        automaticWallpaperChangerUseCase.getIntervalAsString()))
    automaticWallpaperChangerUseCase.startAutomaticWallpaperChangerProcess()
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

}
