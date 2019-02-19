package zebrostudio.wallr100.domain.interactor

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository

interface AuthenticatePurchaseUseCase {
  fun buildUseCaseCompletable(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Completable
}

class AuthenticatePurchaseInteractor(
  private val wallrRepository: WallrRepository
) : AuthenticatePurchaseUseCase {

  override fun buildUseCaseCompletable(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Completable {
    return wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)
        .subscribeOn(Schedulers.io())
  }

}