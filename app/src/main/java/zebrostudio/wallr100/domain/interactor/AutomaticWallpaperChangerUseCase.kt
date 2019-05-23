package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import android.os.Environment
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService
import zebrostudio.wallr100.android.service.WALLPAPER_CHANGER_INTERVALS_LIST
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

interface AutomaticWallpaperChangerUseCase {
  fun attachService(automaticWallpaperChangerService: AutomaticWallpaperChangerService)
  fun detachService()
  fun handleServiceCreated()
  fun getInterval(): Long
}

const val INDEX_OF_FIRST_ELEMENT_IN_LIST = 0
const val INDEX_UNDERFLOW = -1

class AutomaticWallpaperChangerInteractor(
  private val wallpaperSetter: WallpaperSetter,
  private val wallrRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) : AutomaticWallpaperChangerUseCase {

  internal var lastWallpaperChangeTime: Long = System.currentTimeMillis()
  private var timerDisposable: Disposable? = null
  private var wallpaperChangerDisposable: Disposable? = null
  private var automaticWallpaperChangerService: AutomaticWallpaperChangerService? = null
  private val timeCheckerDelay: Long = 120000

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
    logToFile("handle service created")
    println("handle service created")
    timerDisposable = Observable.timer(timeCheckerDelay, TimeUnit.MILLISECONDS)
        .repeat()
        .doOnNext {
          logToFile(
              "do on next observable with current time ${System.currentTimeMillis()} and lastchanged time $lastWallpaperChangeTime")
          println(
              "do on next observable with current time ${System.currentTimeMillis()} and lastchanged time $lastWallpaperChangeTime")
          if (System.currentTimeMillis() - lastWallpaperChangeTime >= getInterval()) {
            logToFile("changer should be called")
            changeWallpaper()
          }
        }.subscribe()
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
        .doOnSubscribe {
          logToFile("Wallpaper Changer Subscribed")
          println("Wallpaper Changer Subscribed")
        }
        .doOnSuccess {
          logToFile("Wallpaper changed")
          println("Wallpaper changed")
          wallpaperSetter.setWallpaper(it)
          if (!it.isRecycled) {
            it.recycle()
          }
        }.observeOn(postExecutionThread.scheduler)
        .subscribe({
          lastWallpaperChangeTime = System.currentTimeMillis()
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

  companion object {
    fun logToFile(message: String) {

      val currentDateTime = System.currentTimeMillis()
      val currentDate = Date(currentDateTime)
      val df = SimpleDateFormat("dd:MM:yy:HH:mm:ss")
      val modifiedMessage = "$message on ${df.format(currentDate)}"

      val file =
          File(Environment.getExternalStorageDirectory().path + File.separator + "WallrLog.txt")
      if (!file.exists()) {
        file.createNewFile()
      }

      try {
        //BufferedWriter for performance, true to set append to file flag
        val buf = BufferedWriter(FileWriter(file, true))
        buf.append(modifiedMessage)
        buf.newLine()
        buf.close()
      } catch (e: IOException) {
        e.printStackTrace()
      }

    }
  }
}