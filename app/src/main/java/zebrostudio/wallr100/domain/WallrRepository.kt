package zebrostudio.wallr100.domain

import io.reactivex.Completable

interface WallrRepository {

  fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Completable

  fun updateUserPurchaseStatus(): Boolean
  fun isUserPremium(): Boolean

}