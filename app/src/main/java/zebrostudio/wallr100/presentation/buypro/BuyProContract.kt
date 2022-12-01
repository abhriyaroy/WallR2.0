package zebrostudio.wallr100.presentation.buypro

import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView

interface BuyProContract {

  interface BuyProView : BaseView {
    fun showInvalidPurchaseError()
    fun showUnableToVerifyPurchaseError()
    fun showNoInternetErrorMessage(premiumOperationType: PremiumTransactionType)
    fun showGenericVerificationError()
    fun showSuccessfulTransactionMessage(proTransactionType: PremiumTransactionType)
    fun showWaitLoader(proTransactionType: PremiumTransactionType)
    fun dismissWaitLoader()
    fun finishWithResult()
    fun isBillerReady(): Boolean
    fun launchPurchase()
    fun launchRestore()
    fun initBiller(isForced : Boolean = false)
  }

  interface BuyProPresenter : BasePresenter<BuyProView> {

    fun handlePurchaseClicked()
    fun handleRestoreClicked()
    fun verifyTransaction(
      responseCode : Int,
      packageName: String,
      skuId: String,
      purchaseToken: String,
      premiumTransactionType: PremiumTransactionType
    )
//    fun handlePurchaseUpdationEvent()
  }

}