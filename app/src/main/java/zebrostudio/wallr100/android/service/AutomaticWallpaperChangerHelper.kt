package zebrostudio.wallr100.android.service

import io.reactivex.Completable
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.ExecutionThread

interface AutomaticWallpaperChangerHelper {
  fun getWallpaperChangerInterval(): Long
  fun setWallpaper(): Completable
}

class AutomaticWallpaperChangerHelperImpl(
  private val wallrRepository: WallrRepository,
  private val wallpaperSetter: WallpaperSetter,
  private val executionThread: ExecutionThread
) : AutomaticWallpaperChangerHelper {

  override fun getWallpaperChangerInterval(): Long {
    return wallrRepository.getWallpaperChangerInterval()
  }

  override fun setWallpaper(): Completable {
    return Completable.create {
      Thread.sleep(1800000)
      it.onComplete()
    }.subscribeOn(executionThread.ioScheduler)
  }

}