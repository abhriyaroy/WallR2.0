package zebrostudio.wallr100.presentation.buypro

import zebrostudio.wallr100.android.BasePresenter
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity

interface BuyProContract {

  interface BuyProView {
    fun showWaitLoader(premiumOperationType: BuyProActivity.Companion.ProTransactionType)
    fun showTryRestoringInfo()
    fun showInvalidPurchaseError()
    fun showUnableToVerifyPurchaseError()
    fun showGenericVerificationError()
    fun showSuccessfulTransactionMessage(
      proTransactionType: BuyProActivity.Companion.ProTransactionType
    )
    fun dismissWaitLoader()

    fun finishWithResult()
  }

  interface BuyProPresenter : BasePresenter<BuyProView> {
    fun verifyPurchaseIfSuccessful(
      packageName: String,
      skuId: String,
      purchaseToken: String,
      proTransactionType: BuyProActivity.Companion.ProTransactionType
    )
  }

}