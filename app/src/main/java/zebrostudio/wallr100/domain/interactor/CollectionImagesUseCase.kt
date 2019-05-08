package zebrostudio.wallr100.domain.interactor

import android.net.Uri
import io.reactivex.Single
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageModel

interface CollectionImagesUseCase {
  fun getAllImages(): Single<List<CollectionsImageModel>>
  fun addImage(imagesUriList: List<Uri>): Single<List<CollectionsImageModel>>
  fun reorderImage(collectionImagesModelList: List<CollectionsImageModel>)
      : Single<List<CollectionsImageModel>>

  fun deleteImages(collectionImagesModelList: List<CollectionsImageModel>)
      : Single<List<CollectionsImageModel>>
}

class CollectionsImagesInteractor(
  private val wallrRepository: WallrRepository
) : CollectionImagesUseCase {

  override fun getAllImages(): Single<List<CollectionsImageModel>> {
    return wallrRepository.getImagesInCollection()
  }

  override fun addImage(imagesUriList: List<Uri>): Single<List<CollectionsImageModel>> {
    return wallrRepository.addImagesToCollection(imagesUriList)
  }

  override fun reorderImage(collectionImagesModelList: List<CollectionsImageModel>)
      : Single<List<CollectionsImageModel>> {
    return wallrRepository.reorderInCollection()
  }

  override fun deleteImages(collectionImagesModelList: List<CollectionsImageModel>)
      : Single<List<CollectionsImageModel>> {
    return wallrRepository.deleteImageFromCollection()
  }

}