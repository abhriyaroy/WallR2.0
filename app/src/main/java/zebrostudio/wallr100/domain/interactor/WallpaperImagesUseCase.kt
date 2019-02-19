package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository
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
  private val wallrDataRepository: WallrRepository
) : WallpaperImagesUseCase {

  override fun exploreImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getExplorePictures()
      .subscribeOn(Schedulers.io())

  override fun recentImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getRecentPictures()
      .subscribeOn(Schedulers.io())

  override fun popularImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getPopularPictures()
      .subscribeOn(Schedulers.io())

  override fun standoutImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getStandoutPictures()
      .subscribeOn(Schedulers.io())

  override fun buildingsImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getBuildingsPictures()
      .subscribeOn(Schedulers.io())

  override fun foodImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getFoodPictures()
      .subscribeOn(Schedulers.io())

  override fun natureImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getNaturePictures()
      .subscribeOn(Schedulers.io())

  override fun objectsImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getObjectsPictures()
      .subscribeOn(Schedulers.io())

  override fun peopleImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getPeoplePictures()
      .subscribeOn(Schedulers.io())

  override fun technologyImagesSingle(): Single<List<ImageModel>> = wallrDataRepository
      .getTechnologyPictures()
      .subscribeOn(Schedulers.io())

}