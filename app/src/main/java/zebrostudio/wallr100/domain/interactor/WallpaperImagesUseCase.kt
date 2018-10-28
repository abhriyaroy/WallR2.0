package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.model.images.ImageModel

interface WallpaperImagesUseCase {
  fun getExploreImages(): Single<List<ImageModel>>
}

class WallpaperImagesInteractor(
  private val wallrDataRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) : WallpaperImagesUseCase {

  override fun getExploreImages(): Single<List<ImageModel>> = wallrDataRepository
      .getExplorePictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

}