package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import io.reactivex.Single
import zebrostudio.wallr100.domain.WallrRepository

interface AutomaticWallpaperChangerUseCase {
  fun getWallpaperBitmap(): Single<Bitmap>
  fun getInterval(): Long
}

const val INDEX_OF_FIRST_ELEMENT_IN_LIST = 0
const val INDEX_UNDERFLOW = -1

class AutomaticWallpaperChangerInteractor(private val wallrRepository: WallrRepository) :
    AutomaticWallpaperChangerUseCase {

  override fun getWallpaperBitmap(): Single<Bitmap> {
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

  override fun getInterval(): Long {
    return wallrRepository.getWallpaperChangerInterval()
  }

}