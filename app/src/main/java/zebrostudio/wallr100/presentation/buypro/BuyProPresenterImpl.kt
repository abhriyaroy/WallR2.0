package zebrostudio.wallr100.presentation.buypro

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.data.customexceptions.InvalidPurchaseException
import zebrostudio.wallr100.data.customexceptions.UnableToVerifyPurchaseException
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase

class BuyProPresenterImpl(
  private var authenticatePurchaseUseCase: AuthenticatePurchaseUseCase,
  private var userPremiumStatusUseCase: UserPremiumStatusUseCase
) : BuyProContract.BuyProPresenter {

  private var buyProView: BuyProContract.BuyProView? = null

  override fun attachView(view: BuyProContract.BuyProView) {
    buyProView = view
  }

  override fun detachView() {
    buyProView = null
  }

  override fun verifyPurchaseIfSuccessful(
    packageName: String,
    skuId: String,
    purchaseToken: String,
    proTransactionType: BuyProActivity.Companion.ProTransactionType
  ) {
    authenticatePurchaseUseCase.buildUseCaseSingle(packageName, skuId, purchaseToken)
        .autoDisposable(buyProView?.getScope()!!)
        .subscribe({
          handleSuccessfulVerification(proTransactionType)
          buyProView?.dismissWaitLoader()
        }, {
          when (it) {
            is InvalidPurchaseException -> buyProView?.showInvalidPurchaseError()
            is UnableToVerifyPurchaseException -> buyProView?.showUnableToVerifyPurchaseError()
            else -> buyProView?.showGenericVerificationError()
          }
          buyProView?.dismissWaitLoader()
        })
  }

  private fun handleSuccessfulVerification(
    proTransactionType: BuyProActivity.Companion.ProTransactionType
  ) {
    if (userPremiumStatusUseCase.saveUserAsPro()) {
      buyProView?.showSuccessfulTransactionMessage(proTransactionType)
      buyProView?.finishWithResult()
    }
  }

}