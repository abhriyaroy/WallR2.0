package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Completable
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

  fun getImageBitmap(collectionsImageModel: CollectionsImageModel): Single<Bitmap>
  fun saveCrystallizedImage(collectionsImageModel: CollectionsImageModel)
      : Single<List<CollectionsImageModel>>

  fun startAutomaticWallpaperChanger(): Completable
  fun stopAutomaticWallpaperChanger(): Completable
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
    return wallrRepository.reorderInCollection(collectionImagesModelList)
  }

  override fun deleteImages(collectionImagesModelList: List<CollectionsImageModel>)
      : Single<List<CollectionsImageModel>> {
    return wallrRepository.deleteImageFromCollection(collectionImagesModelList)
  }

  override fun getImageBitmap(collectionsImageModel: CollectionsImageModel): Single<Bitmap> {
    return wallrRepository.getBitmapFromDatabaseImage(collectionsImageModel)
  }

  override fun saveCrystallizedImage(collectionsImageModel: CollectionsImageModel)
      : Single<List<CollectionsImageModel>> {
    return wallrRepository.saveCrystallizedImageInDatabase(collectionsImageModel)
  }

  override fun startAutomaticWallpaperChanger(): Completable {
    return wallrRepository.enableWallpaperChangerService()
  }

  override fun stopAutomaticWallpaperChanger(): Completable {
    return wallrRepository.disableWallpaperChangerService()
  }

}