package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.WallrRepository

class AuthenticatePurchaseUseCase(
  private val wallrRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) {

  fun buildUseCaseSingle(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Single<Any> {
    return wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
  }

}