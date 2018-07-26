package zebrostudio.wallr100.data

import io.reactivex.Completable
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.PurchaseAuthResponse

class WallrDataRepository : WallrRepository {

  override fun authenticatePurchase(purchaseAuthResponse: PurchaseAuthResponse): Completable {
    // Change these methods
    return Completable.complete()
  }

  override fun authenticateRestorePurchase(purchaseAuthResponse: PurchaseAuthResponse): Completable {
    // Change these methods
    return Completable.complete()
  }

}