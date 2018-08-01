package zebrostudio.wallr100.domain

import io.reactivex.Single
import zebrostudio.wallr100.domain.model.PurchaseAuthModel

interface WallrRepository {

  fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Single<PurchaseAuthModel>

  fun isInternetAvailable() : Boolean

}