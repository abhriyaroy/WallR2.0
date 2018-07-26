package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import org.buffer.android.boilerplate.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.PurchaseAuthResponse

class AuthenticatePurchaseUseCase(
  private var wallrRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) {

  private val disposables = CompositeDisposable()

  fun buildUseCaseObservable(
    packageName: String,
    skuId: String,
    purchaseToken: String,
    disposableSingleObserver: DisposableSingleObserver<PurchaseAuthResponse>
  ): Single<PurchaseAuthResponse>? {

    val single = wallrRepository.authenticatePurchase(packageName, skuId, purchaseToken)
        ?.subscribeOn(Schedulers.io())
        ?.observeOn(postExecutionThread.scheduler)
    addDisposable(single?.subscribeWith(disposableSingleObserver))
    return single
  }

  private fun addDisposable(disposable: DisposableSingleObserver<PurchaseAuthResponse>?) {
    if (disposable != null) {
      disposables.add(disposable)
    }
  }

  fun dispose() {
    if (!disposables.isDisposed) disposables.dispose()
  }

}