package zebrostudio.wallr100.presentation.buypro

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType.PURCHASE
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType.RESTORE
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig.Companion.ITEM_SKU_TEST
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.buypro.BuyProContract.BuyProPresenter

class BuyProPresenterImpl(
  private val authenticatePurchaseUseCase: AuthenticatePurchaseUseCase,
  private val userPremiumStatusUseCase: UserPremiumStatusUseCase,
  private val postExecutionThread: PostExecutionThread
) : BuyProPresenter {

  private var buyProView: BuyProContract.BuyProView? = null

  override fun attachView(view: BuyProContract.BuyProView) {
    buyProView = view
  }

  override fun detachView() {
    buyProView = null
  }

  override fun handlePurchaseClicked() {
    if (buyProView?.isBillerReady() == true) {
      if (buyProView?.internetAvailability() == true) {
        buyProView?.showWaitLoader(PURCHASE)
        buyProView?.launchPurchase()
      } else {
        buyProView?.showNoInternetErrorMessage(PURCHASE)
      }
    } else {
      buyProView?.showGenericVerificationError()
    }
  }

  override fun handleRestoreClicked() {
    if (buyProView?.isBillerReady() == true) {
      if (buyProView?.internetAvailability() == true) {
        buyProView?.showWaitLoader(RESTORE)
        buyProView?.launchRestore()
      } else {
        buyProView?.showNoInternetErrorMessage(RESTORE)
      }
    } else {
      buyProView?.showGenericVerificationError()
      buyProView?.initBiller(true)
    }
  }

  override fun verifyTransaction(
    responseCode : Int,
    packageName: String,
    skuId: String,
    purchaseToken: String,
    premiumTransactionType: PremiumTransactionType
  ) {
    if(responseCode == 7 ||
      (packageName == "zebrostudio.wallr100"
              && skuId == PurchaseTransactionConfig.ITEM_SKU
              && responseCode == 0)){
      handleSuccessfulVerification(premiumTransactionType)
    } else {
      buyProView?.showInvalidPurchaseError()
    }
    buyProView?.dismissWaitLoader()

//    authenticatePurchaseUseCase.authenticatePurchaseCompletable(packageName, skuId,
//      purchaseToken)
//        .observeOn(postExecutionThread.scheduler)
//        .autoDisposable(buyProView?.getScope()!!)
//        .subscribe({
//          handleSuccessfulVerification(premiumTransactionType)
//          buyProView?.dismissWaitLoader()
//        }, {
//          when (it) {
//            is InvalidPurchaseException -> buyProView?.showInvalidPurchaseError()
//            is UnableToVerifyPurchaseException -> buyProView?.showUnableToVerifyPurchaseError()
//            else -> buyProView?.showGenericVerificationError()
//          }
//          buyProView?.dismissWaitLoader()
//        })
  }

  private fun handleSuccessfulVerification(premiumTransactionType: PremiumTransactionType) {
    if (userPremiumStatusUseCase.updateUserPurchaseStatus()) {
      buyProView?.showSuccessfulTransactionMessage(premiumTransactionType)
      buyProView?.finishWithResult()
    } else {
      buyProView?.showGenericVerificationError()
    }
  }

}