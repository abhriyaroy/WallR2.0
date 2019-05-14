package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import io.reactivex.Single
import zebrostudio.wallr100.domain.WallrRepository

interface AutomaticWallpaperChangerUseCase {
  fun getWallpaperBitmap(): Single<Bitmap>
  fun getInterval(): Long
}

const val LIST_FIRST_ELEMENT_INDEX = 0
const val INDEX_NOT_INITIALIZED = -1

class AutomaticWallpaperChangerInteractor(private val wallrRepository: WallrRepository) :
    AutomaticWallpaperChangerUseCase {

  override fun getWallpaperBitmap(): Single<Bitmap> {
    return wallrRepository.getImagesInCollection()
        .flatMap { list ->
          println("subscribe flatmap")
          var index = INDEX_NOT_INITIALIZED
          wallrRepository.getLastUsedWallpaperUid().let {
            for (position in LIST_FIRST_ELEMENT_INDEX until list.size) {
              if (it == list[position].uid) {
                index = position.inc()
                break
              }
            }
            if (index >= list.size.dec() || index == INDEX_NOT_INITIALIZED) {
              index = LIST_FIRST_ELEMENT_INDEX
            }
          }
          list[index].let {
            wallrRepository.setLastUsedWallpaperUid(it.uid)
            println("subscribe flatmap complete")
            wallrRepository.getBitmapFromDatabaseImage(list[index])
          }
        }
  }

  override fun getInterval(): Long {
    return wallrRepository.getWallpaperChangerInterval()
  }

}