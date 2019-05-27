package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_INTERVALS_LIST
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.ExecutionThread
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import java.util.concurrent.TimeUnit

interface AutomaticWallpaperChangerUseCase {
  fun attachService(automaticWallpaperChangerService: AutomaticWallpaperChangerService)
  fun detachService()
  fun getIntervalString(): String
  fun handleServiceCreated()
}

const val INDEX_OF_FIRST_ELEMENT_IN_LIST = 0
const val INDEX_UNDERFLOW = -1
const val TIME_CHECKER_INTERVAL: Long = 120000

class AutomaticWallpaperChangerInteractor(
  private val wallpaperSetter: WallpaperSetter,
  private val wallrRepository: WallrRepository,
  private val resourceUtils: ResourceUtils,
  private val executionThread: ExecutionThread,
  private val postExecutionThread: PostExecutionThread
) : AutomaticWallpaperChangerUseCase {

  private var timerDisposable: Disposable? = null
  private var wallpaperChangerDisposable: Disposable? = null
  private var automaticWallpaperChangerService: AutomaticWallpaperChangerService? = null

  override fun attachService(automaticWallpaperChangerService: AutomaticWallpaperChangerService) {
    this.automaticWallpaperChangerService = automaticWallpaperChangerService
  }

  override fun detachService() {
    automaticWallpaperChangerService = null
    if (timerDisposable?.isDisposed == false) {
      timerDisposable?.dispose()
    }
  }

  override fun handleServiceCreated() {
    timerDisposable =
        Observable.timer(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS,
            executionThread.computationScheduler)
            .repeat()
            .doOnNext {
              if (System.currentTimeMillis() - wallrRepository.getLastWallpaperChangeTimeStamp()
                  >= getInterval()) {
                changeWallpaper()
              }
            }
            .subscribe()
  }

  override fun getIntervalString(): String {
    return when (getInterval()) {
      WALLPAPER_CHANGER_INTERVALS_LIST[1] -> resourceUtils.getStringResource(
          R.string.wallpaper_changer_service_interval_1_hour)
      WALLPAPER_CHANGER_INTERVALS_LIST[2] -> resourceUtils.getStringResource(
          R.string.wallpaper_changer_service_interval_6_hours)
      WALLPAPER_CHANGER_INTERVALS_LIST[3] -> resourceUtils.getStringResource(
          R.string.wallpaper_changer_service_interval_1_day)
      WALLPAPER_CHANGER_INTERVALS_LIST[4] -> resourceUtils.getStringResource(
          R.string.wallpaper_changer_service_interval_3_days)
      else -> resourceUtils.getStringResource(
          R.string.wallpaper_changer_service_interval_30_minutes)
    }
  }

  fun getInterval(): Long {
    wallrRepository.getWallpaperChangerInterval().let {
      return if (WALLPAPER_CHANGER_INTERVALS_LIST.contains(it)) {
        it
      } else {
        WALLPAPER_CHANGER_INTERVALS_LIST.first()
      }
    }
  }

  private fun changeWallpaper() {
    wallpaperChangerDisposable = getWallpaperBitmap()
        .doOnSuccess {
          wallpaperSetter.setWallpaper(it)
          if (!it.isRecycled) {
            it.recycle()
          }
        }.observeOn(postExecutionThread.scheduler)
        .subscribe({
          wallrRepository.updateLastWallpaperChangeTimeStamp(System.currentTimeMillis())
        }, {
          automaticWallpaperChangerService?.stopService()
        })
  }

  private fun getWallpaperBitmap(): Single<Bitmap> {
    return wallrRepository.getImagesInCollection()
        .flatMap { list ->
          var index = INDEX_UNDERFLOW
          wallrRepository.getLastUsedWallpaperUid().let {
            for (position in INDEX_OF_FIRST_ELEMENT_IN_LIST until list.size) {
              if (it == list[position].uid) {
                index = position.inc()
                break
              }
            }
            if (index >= list.size || index == INDEX_UNDERFLOW) {
              index = INDEX_OF_FIRST_ELEMENT_IN_LIST
            }
          }
          list[index].let {
            wallrRepository.setLastUsedWallpaperUid(it.uid)
            wallrRepository.getBitmapFromDatabaseImage(it)
          }
        }
  }
}