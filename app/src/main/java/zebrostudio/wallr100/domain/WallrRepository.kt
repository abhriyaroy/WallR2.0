package zebrostudio.wallr100.domain

import io.reactivex.Single
import zebrostudio.wallr100.domain.model.PurchaseAuthResponse

interface WallrRepository {

  fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Single<PurchaseAuthResponse>

}