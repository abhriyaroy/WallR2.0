package zebrostudio.wallr100.domain.interactor

import io.reactivex.Completable
import zebrostudio.wallr100.domain.WallrRepository

interface AuthenticatePurchaseUseCase {
  fun authenticatePurchaseCompletable(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Completable
}

class AuthenticatePurchaseInteractor(
  private val wallrRepository: WallrRepository
) : AuthenticatePurchaseUseCase {

  override fun authenticatePurchaseCompletable(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Completable {
    return wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)
  }

}