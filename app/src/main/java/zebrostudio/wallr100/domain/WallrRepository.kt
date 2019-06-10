package zebrostudio.wallr100.domain

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import zebrostudio.wallr100.domain.model.RestoreColorsModel
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageModel
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.domain.model.searchpictures.SearchPicturesModel
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType

interface WallrRepository {

  fun isAppOpenedForTheFirstTime(): Boolean
  fun saveAppPreviouslyOpenedState()
  fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Completable

  fun updateUserPurchaseStatus(): Boolean
  fun isUserPremium(): Boolean

  fun getSearchPictures(query: String): Single<List<SearchPicturesModel>>

  fun getExplorePictures(): Single<List<ImageModel>>
  fun getRecentPictures(): Single<List<ImageModel>>
  fun getPopularPictures(): Single<List<ImageModel>>
  fun getStandoutPictures(): Single<List<ImageModel>>
  fun getBuildingsPictures(): Single<List<ImageModel>>
  fun getFoodPictures(): Single<List<ImageModel>>
  fun getNaturePictures(): Single<List<ImageModel>>
  fun getObjectsPictures(): Single<List<ImageModel>>
  fun getPeoplePictures(): Single<List<ImageModel>>
  fun getTechnologyPictures(): Single<List<ImageModel>>

  fun getImageBitmap(): Single<Bitmap>
  fun getImageBitmap(link: String): Observable<ImageDownloadModel>
  fun getCacheImageBitmap(): Single<Bitmap>
  fun getShortImageLink(link: String): Single<String>
  fun clearImageCaches(): Completable
  fun cancelImageBitmapFetchOperation()

  fun getCacheSourceUri(): Single<Uri>
  fun getCacheResultUri(): Single<Uri>
  fun getShareableImageUri(): Single<Uri>

  fun getBitmapFromUri(uri: Uri?): Single<Bitmap>
  fun downloadImage(link: String): Completable
  fun crystallizeImage(): Single<Pair<Boolean, Bitmap>>
  fun saveCachedImageToDownloads(): Completable
  fun isCrystallizeDescriptionShown(): Boolean
  fun saveCrystallizeDescriptionShown()
  fun checkIfDownloadIsInProgress(link: String): Boolean

  fun saveImageToCollections(
    data: String,
    collectionsImageType: CollectionsImageType
  ): Completable

  fun isMultiColorModesHintShown(): Boolean
  fun saveMultiColorModesHintShownState()
  fun isCustomMinimalColorListPresent(): Boolean
  fun getCustomMinimalColorList(): Single<List<String>>
  fun getDefaultMinimalColorList(): Single<List<String>>
  fun saveCustomMinimalColorList(colors: List<String>): Completable
  fun modifyColorList(
    colors: List<String>,
    selectedIndicesMap: HashMap<Int, String>
  ): Single<List<String>>

  fun restoreDeletedColors(): Single<RestoreColorsModel>
  fun getSingleColorBitmap(hexValue: String): Single<Bitmap>
  fun getMultiColorBitmap(
    hexValueList: List<String>,
    multiColorImageType: MultiColorImageType
  ): Single<Bitmap>

  fun getImagesInCollection(): Single<List<CollectionsImageModel>>
  fun addImagesToCollection(uriList: List<Uri>): Single<List<CollectionsImageModel>>
  fun reorderImagesInCollectionDatabase(collectionImagesModelList: List<CollectionsImageModel>)
      : Single<List<CollectionsImageModel>>

  fun deleteImageFromCollection(collectionsImageModelList: List<CollectionsImageModel>)
      : Single<List<CollectionsImageModel>>

  fun getAutomaticWallpaperChangerState(): Boolean
  fun isCollectionReorderHintDisplayedBefore(): Boolean
  fun saveCollectionReorderHintShownState()
  fun getBitmapFromDatabaseImage(collectionsImageModel: CollectionsImageModel): Single<Bitmap>
  fun saveCrystallizedImageInDatabase(collectionsImageModel: CollectionsImageModel)
      : Single<List<CollectionsImageModel>>

  fun getWallpaperChangerInterval(): Long
  fun setWallpaperChangerInterval(interval: Long)
  fun getLastUsedWallpaperUid(): Long
  fun setLastUsedWallpaperUid(uid: Long)
  fun saveAutomaticWallpaperChangerEnabledState()
  fun saveAutomaticWallpaperChangerDisabledState()
  fun wasAutomaticWallpaperChangerEnabled(): Boolean
  fun getLastWallpaperChangeTimeStamp(): Long
  fun updateLastWallpaperChangeTimeStamp(timeStamp: Long)
}