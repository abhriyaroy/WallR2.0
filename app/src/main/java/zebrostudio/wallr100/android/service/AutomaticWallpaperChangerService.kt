package zebrostudio.wallr100.android.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.support.annotation.Nullable
import dagger.android.AndroidInjection
import zebrostudio.wallr100.android.notification.NotificationFactory
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

}