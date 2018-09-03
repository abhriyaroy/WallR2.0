package zebrostudio.wallr100.domain

import io.reactivex.Observable
import io.reactivex.Single
import zebrostudio.wallr100.domain.model.SearchPicturesModel

interface WallrRepository {

  fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Single<Any>

  fun updateUserPurchaseStatus(): Boolean
  fun isUserPremium(): Boolean

  fun getPictures(query: String): Single<List<SearchPicturesModel>>

}