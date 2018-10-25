package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import zebrostudio.wallr100.data.WallrDataRepository
import zebrostudio.wallr100.domain.model.images.ImageModel

interface WallpaperImagesUseCase {
  fun getExploreImages(): Single<List<ImageModel>>
}

class WallpaperImagesInteractor(private var wallrDataRepository: WallrDataRepository) :
    WallpaperImagesUseCase {

  override fun getExploreImages(): Single<List<ImageModel>> = wallrDataRepository.getExplorePictures()
  
}