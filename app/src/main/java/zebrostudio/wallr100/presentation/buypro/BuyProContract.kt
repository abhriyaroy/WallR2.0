package zebrostudio.wallr100.presentation.buypro

import zebrostudio.wallr100.android.BasePresenter
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity

interface BuyProContract {

  interface BuyProView {
    fun showWaitLoader(premiumOperationType: BuyProActivity.Companion.PremiumOperationType)
    fun showTryRestoringInfo()
    fun showInvalidPurchaseError()
    fun showUnableToVerifyPurchaseError()
    fun showGenericVerificationError()

    fun dismissWaitLoader()
  }

  interface BuyProPresenter : BasePresenter<BuyProView> {
    fun verifyPurchaseIfSuccessful(
      packageName: String,
      skuId: String,
      purchaseToken: String
    )
  }

}