package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.model.images.ImageModel

interface WallpaperImagesUseCase {
  fun exploreImagesSingle(): Single<List<ImageModel>>
  fun recentImagesSingle(): Single<List<ImageModel>>
  fun popularImagesSingle(): Single<List<ImageModel>>
  fun standoutImagesSingle(): Single<List<ImageModel>>
  fun buildingsImagesSingle(): Single<List<ImageModel>>
  fun foodImagesSingle(): Single<List<ImageModel>>
  fun natureImagesSingle(): Single<List<ImageModel>>
  fun objectsImagesSingle(): Single<List<ImageModel>>
  fun peopleImagesSingle(): Single<List<ImageModel>>
  fun technologyImagesSingle(): Single<List<ImageModel>>
}

class WallpaperImagesInteractor(
  private val wallrDataRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) : WallpaperImagesUseCase {

  override fun exploreImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getExplorePictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun recentImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getRecentPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun popularImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getPopularPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun standoutImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getStandoutPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun buildingsImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getBuildingsPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun foodImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getFoodPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun natureImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getNaturePictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun objectsImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getObjectsPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun peopleImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getPeoplePictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun technologyImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getTechnologyPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

}