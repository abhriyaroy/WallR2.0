package zebrostudio.wallr100.domain

import io.reactivex.Completable
import zebrostudio.wallr100.domain.model.PurchaseAuthResponse

interface WallrRepository {

  fun authenticatePurchase(purchaseAuthResponse: PurchaseAuthResponse): Completable

  fun authenticateRestorePurchase(purchaseAuthResponse: PurchaseAuthResponse): Completable

}