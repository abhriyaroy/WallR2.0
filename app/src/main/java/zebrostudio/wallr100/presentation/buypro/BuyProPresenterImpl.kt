package zebrostudio.wallr100.presentation.buypro

import io.reactivex.disposables.CompositeDisposable
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.presentation.entity.PurchaseAuthPresentationEntity
import zebrostudio.wallr100.presentation.mapper.ProAuthPresentationMapperImpl

class BuyProPresenterImpl(
  private var authenticatePurchaseUseCase: AuthenticatePurchaseUseCase,
  private var presentationMapper: ProAuthPresentationMapperImpl
) : BuyProContract.BuyProPresenter {

  private var buyProView: BuyProContract.BuyProView? = null
  private var compositeDisposable = CompositeDisposable()

  override fun attachView(view: BuyProContract.BuyProView) {
    buyProView = view
  }

  override fun detachView() {
    compositeDisposable.dispose()
    buyProView = null
  }

  override fun verifyPurchaseIfSuccessful(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ) {
    authenticatePurchaseUseCase.buildUseCaseSingle(packageName, skuId, purchaseToken)
        .map {
          presentationMapper.mapToPresentationEntity(it)
        }
        .subscribe({ response: PurchaseAuthPresentationEntity ->
          if (response.status == "error"
              && response.message == "something went wrong"
              && (response.errorCode == 4004 || response.errorCode == 4010)) {
            buyProView?.showInvalidPurchaseError()
          } else if (response.status == "success") {
            handleSuccessfulPurchaseVerification()
          } else {
            buyProView?.showUnableToVerifyPurchaseError()
          }
          buyProView?.dismissWaitLoader()
        }, { _: Throwable ->
          buyProView?.showGenericVerificationError()
          buyProView?.dismissWaitLoader()
        })
        .let { disposable ->
          compositeDisposable.add(
              disposable
          )
        }
  }

  private fun handleSuccessfulPurchaseVerification() {
    // ToDo : Handle successful purchase

  }

}