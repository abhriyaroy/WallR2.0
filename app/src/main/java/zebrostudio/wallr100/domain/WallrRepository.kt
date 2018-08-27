package zebrostudio.wallr100.domain

import io.reactivex.Single

interface WallrRepository {

  fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Single<Any>

  fun updateUserPurchaseStatus(): Boolean
  fun isUserPremium(): Boolean

}