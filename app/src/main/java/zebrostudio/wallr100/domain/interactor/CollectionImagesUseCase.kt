package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Single
import zebrostudio.wallr100.android.service.ServiceManager
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerIntervalUpdateResultState.INTERVAL_UPDATED
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerIntervalUpdateResultState.SERVICE_RESTARTED
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

  fun isAutomaticWallpaperChangerRunning(): Boolean
  fun startAutomaticWallpaperChanger()
  fun stopAutomaticWallpaperChanger()
  fun getAutomaticWallpaperChangerInterval(): Long
  fun setAutomaticWallpaperChangerInterval(interval: Long)
      : AutomaticWallpaperChangerIntervalUpdateResultState

  fun saveAutomaticWallpaperChangerStateAsEnabled()
  fun saveAutomaticWallpaperChangerStateAsDisabled()
  fun wasAutomaticWallpaperChangerEnabled(): Boolean
}

class CollectionsImagesInteractor(
  private val serviceManager: ServiceManager,
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
    return wallrRepository.reorderImagesInCollectionDatabase(collectionImagesModelList)
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

  override fun isAutomaticWallpaperChangerRunning(): Boolean {
    return serviceManager.isAutomaticWallpaperChangerRunning()
  }

  override fun startAutomaticWallpaperChanger() {
    serviceManager.startAutomaticWallpaperChangerService()
  }

  override fun stopAutomaticWallpaperChanger() {
    serviceManager.stopAutomaticWallpaperChangerService()
  }

  override fun getAutomaticWallpaperChangerInterval(): Long {
    return wallrRepository.getWallpaperChangerInterval()
  }

  override fun setAutomaticWallpaperChangerInterval(
    interval: Long
  ): AutomaticWallpaperChangerIntervalUpdateResultState {
    wallrRepository.setWallpaperChangerInterval(interval)
    if (isAutomaticWallpaperChangerRunning()) {
      serviceManager.stopAutomaticWallpaperChangerService()
      serviceManager.startAutomaticWallpaperChangerService()
      return SERVICE_RESTARTED
    }
    return INTERVAL_UPDATED
  }

  override fun saveAutomaticWallpaperChangerStateAsEnabled() {
    wallrRepository.saveAutomaticWallpaperChangerEnabledState()
  }

  override fun saveAutomaticWallpaperChangerStateAsDisabled() {
    wallrRepository.saveAutomaticWallpaperChangerDisabledState()
  }

  override fun wasAutomaticWallpaperChangerEnabled(): Boolean {
    return wallrRepository.wasAutomaticWallpaperChangerEnabled()
  }
}

enum class AutomaticWallpaperChangerIntervalUpdateResultState {
  INTERVAL_UPDATED,
  SERVICE_RESTARTED
}