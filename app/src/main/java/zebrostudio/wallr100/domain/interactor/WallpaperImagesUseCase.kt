package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.model.images.ImageModel

interface WallpaperImagesUseCase {
  fun getExploreImages(): Single<List<ImageModel>>
  fun getRecentImages(): Single<List<ImageModel>>
  fun getPopularImages(): Single<List<ImageModel>>
  fun getStandoutImages(): Single<List<ImageModel>>
  fun getBuildingsImages(): Single<List<ImageModel>>
  fun getFoodImages(): Single<List<ImageModel>>
  fun getNatureImages(): Single<List<ImageModel>>
  fun getObjectsImages(): Single<List<ImageModel>>
  fun getPeopleImages(): Single<List<ImageModel>>
  fun getTechnologyImages(): Single<List<ImageModel>>
}

class WallpaperImagesInteractor(
  private val wallrDataRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) : WallpaperImagesUseCase {

  override fun getExploreImages(): Single<List<ImageModel>> = wallrDataRepository
      .getExplorePictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun getRecentImages(): Single<List<ImageModel>> = wallrDataRepository
      .getRecentPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun getPopularImages(): Single<List<ImageModel>> = wallrDataRepository
      .getPopularPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun getStandoutImages(): Single<List<ImageModel>> = wallrDataRepository
      .getStandoutPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun getBuildingsImages(): Single<List<ImageModel>> = wallrDataRepository
      .getBuildingsPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun getFoodImages(): Single<List<ImageModel>> = wallrDataRepository
      .getFoodPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun getNatureImages(): Single<List<ImageModel>> = wallrDataRepository
      .getNaturePictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun getObjectsImages(): Single<List<ImageModel>> = wallrDataRepository
      .getObjectsPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun getPeopleImages(): Single<List<ImageModel>> = wallrDataRepository
      .getPeoplePictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

  override fun getTechnologyImages(): Single<List<ImageModel>> = wallrDataRepository
      .getTechnologyPictures()
      .subscribeOn(Schedulers.io())
      .observeOn(postExecutionThread.scheduler)

}