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
import java.util.concurrent.TimeUnit.MILLISECONDS

interface AutomaticWallpaperChangerUseCase {
  fun attachService(automaticWallpaperChangerService: AutomaticWallpaperChangerService)
  fun detachService()
  fun handleServiceStarted()
  fun handleServiceDestroyed()
  fun getInterval(): Long
}

const val INDEX_OF_FIRST_ELEMENT_IN_LIST = 0
const val INDEX_UNDERFLOW = -1
const val TIME_CHECKER_INTERVAL: Long = 300000

class AutomaticWallpaperChangerInteractor(
  private val wallpaperSetter: WallpaperSetter,
  private val wallrRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) : AutomaticWallpaperChangerUseCase {

  internal var lastWallpaperChangeTime: Long = System.currentTimeMillis()
  private var timerDisposable: Disposable? = null
  private var wallpaperChangerDisposable: Disposable? = null
  private var automaticWallpaperChangerService: AutomaticWallpaperChangerService? = null

  override fun attachService(automaticWallpaperChangerService: AutomaticWallpaperChangerService) {
    this.automaticWallpaperChangerService = automaticWallpaperChangerService
  }

  override fun detachService() {
    automaticWallpaperChangerService = null
  }

  override fun handleServiceStarted() {
    timerDisposable = Observable.timer(TIME_CHECKER_INTERVAL, MILLISECONDS)
        .repeat()
        .doOnNext {
          appendLog("on next in timer disposable called")
          if (System.currentTimeMillis() - lastWallpaperChangeTime >= getInterval()) {
            changeWallpaper()
          }
        }
        .doOnError {
          appendLog("on error in timer disposable called ${it.message}")
        }
        .subscribe()
  }

  override fun handleServiceDestroyed() {
    appendLog("on handle service destroyed called")
    if (timerDisposable?.isDisposed != true) {
      timerDisposable?.dispose()
    }
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
    wallpaperChangerDisposable = getWallpaperBitmap().toObservable()
        .delay(getInterval(), MILLISECONDS)
        .repeat()
        .doOnNext {

          appendLog("wallpaper changed ")
          wallpaperSetter.setWallpaper(it)
          lastWallpaperChangeTime = System.currentTimeMillis()
          if (!it.isRecycled) {
            it.recycle()
          }
        }.observeOn(postExecutionThread.scheduler)
        .doOnError {
          automaticWallpaperChangerService?.stopService()
        }
        .subscribe()
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
    fun appendLog(text: String) {
      val logFile =
          File(Environment.getExternalStorageDirectory().path + File.separator + "wallrlog.txt")
      if (!logFile.exists()) {
        try {
          logFile.createNewFile()
        } catch (e: IOException) {
          // TODO Auto-generated catch block
          e.printStackTrace()
        }

      }
      try {
        val currentDate = Date(System.currentTimeMillis())

        val df = SimpleDateFormat("dd:MM:yy:HH:mm:ss")

        val newText = "$text on ${df.format(currentDate)}"

        //BufferedWriter for performance, true to set append to file flag
        val buf = BufferedWriter(FileWriter(logFile, true))
        buf.append(newText)
        buf.newLine()
        buf.close()
      } catch (e: IOException) {
        // TODO Auto-generated catch block
        e.printStackTrace()
      }
    }
  }
}