package zebrostudio.wallr100.domain

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import zebrostudio.wallr100.domain.model.RestoreColorsModel
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.domain.model.searchpictures.SearchPicturesModel

interface WallrRepository {

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

  fun getImageBitmap(link: String): Observable<ImageDownloadModel>
  fun getCacheImageBitmap(): Single<Bitmap>
  fun getShortImageLink(link: String): Single<String>
  fun clearImageCaches(): Completable
  fun cancelImageBitmapFetchOperation()

  fun getCacheSourceUri(): Uri
  fun getCacheResultUri(): Uri

  fun getBitmapFromUri(uri: Uri): Single<Bitmap>
  fun downloadImage(link: String): Completable
  fun crystallizeImage(): Single<Pair<Boolean, Bitmap>>
  fun saveCrystallizedImageToDownloads(): Completable
  fun isCrystallizeDescriptionShown(): Boolean
  fun rememberCrystallizeDescriptionShown()
  fun checkIfDownloadIsInProgress(link: String): Boolean

  fun saveImageToCollections(type: Int, details: String): Completable

  fun isCustomMinimalColorListPresent(): Boolean
  fun getCustomMinimalColorList(): Single<List<String>>
  fun getDefaultMinimalColorList(): Single<List<String>>
  fun saveCustomMinimalColorList(colors: List<String>): Completable
  fun modifyColorList(
    colors: List<String>,
    selectedIndicesMap: HashMap<Int, String>
  ): Single<List<String>>

  fun restoreDeletedColors(): Single<RestoreColorsModel>

}