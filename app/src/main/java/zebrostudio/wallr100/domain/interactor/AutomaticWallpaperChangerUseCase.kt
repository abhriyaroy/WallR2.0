package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_INTERVALS_LIST
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import java.util.concurrent.TimeUnit

interface AutomaticWallpaperChangerUseCase {
  fun attachService(automaticWallpaperChangerService: AutomaticWallpaperChangerService)
  fun detachService()
  fun handleServiceCreated()
  fun getInterval(): Long
}

const val INDEX_OF_FIRST_ELEMENT_IN_LIST = 0
const val INDEX_UNDERFLOW = -1
const val TIME_CHECKER_INTERVAL: Long = 120000

class AutomaticWallpaperChangerInteractor(
  private val wallpaperSetter: WallpaperSetter,
  private val wallrRepository: WallrRepository,
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
    timerDisposable = Observable.timer(TIME_CHECKER_INTERVAL, TimeUnit.MILLISECONDS)
        .repeat()
        .doOnNext {
          if (System.currentTimeMillis() - wallrRepository.getLastWallpaperChangeTimeStamp()
              >= getInterval()) {
            changeWallpaper()
          }
        }
        .subscribe()
  }

  override fun getInterval(): Long {
    wallrRepository.getWallpaperChangerInterval().let {
      return if (WALLPAPER_CHANGER_INTERVALS_LIST.contains(it)) {
        it
      } else {
        WALLPAPER_CHANGER_INTERVALS_LIST.first()
      }
    }
  }

  private fun changeWallpaper() {
    if (wallpaperChangerDisposable?.isDisposed == false) {
      wallpaperChangerDisposable?.dispose()
    }
    wallpaperChangerDisposable = getWallpaperBitmap()
        .doOnSuccess {
          wallpaperSetter.setWallpaper(it)
          if (!it.isRecycled) {
            it.recycle()
          }
        }.observeOn(postExecutionThread.scheduler)
        .subscribe({
          wallrRepository.setLastWallpaperChangeTimeStamp(System.currentTimeMillis())
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