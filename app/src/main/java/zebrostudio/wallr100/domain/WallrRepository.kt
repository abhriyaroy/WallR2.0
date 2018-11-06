package zebrostudio.wallr100.domain

import io.reactivex.Completable
import io.reactivex.Single
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

}