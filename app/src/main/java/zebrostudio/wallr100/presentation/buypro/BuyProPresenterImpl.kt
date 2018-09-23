package zebrostudio.wallr100.presentation.buypro

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType.*
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
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

  override fun notifyPurchaseClicked() {
    if (buyProView?.isIabReady() == true) {
      if (buyProView?.isInternetAvailable() == true) {
        buyProView?.showWaitLoader(PURCHASE)
        buyProView?.launchPurchase()
      } else {
        buyProView?.showNoInternetErrorMessage(PURCHASE)
      }
    } else {
      buyProView?.showGenericVerificationError()
    }
  }

  override fun notifyRestoreClicked() {
    if (buyProView?.isIabReady() == true) {
      if (buyProView?.isInternetAvailable() == true) {
        buyProView?.showWaitLoader(RESTORE)
        buyProView?.launchRestore()
      } else {
        buyProView?.showNoInternetErrorMessage(RESTORE)
      }
    } else {
      buyProView?.showGenericVerificationError()
    }
  }

  override fun verifyPurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String,
    proTransactionType: PremiumTransactionType
  ) {
    authenticatePurchaseUseCase.buildUseCaseCompletable(packageName, skuId, purchaseToken)
        .autoDisposable(buyProView?.getScope()!!)
        .subscribe({
          handleSuccessfulVerification(proTransactionType)
          buyProView?.dismissWaitLoader()
        }, {
          System.out.println(it.toString())
          when (it) {
            is InvalidPurchaseException -> buyProView?.showInvalidPurchaseError()
            is UnableToVerifyPurchaseException -> buyProView?.showUnableToVerifyPurchaseError()
            else -> buyProView?.showGenericVerificationError()
          }
          buyProView?.dismissWaitLoader()
        })
  }

  fun handleSuccessfulVerification(
    premiumTransactionType: PremiumTransactionType
  ) {
    if (userPremiumStatusUseCase.updateUserPurchaseStatus()) {
      buyProView?.showSuccessfulTransactionMessage(premiumTransactionType)
      buyProView?.finishWithResult()
    } else {
      buyProView?.showGenericVerificationError()
    }
  }

}