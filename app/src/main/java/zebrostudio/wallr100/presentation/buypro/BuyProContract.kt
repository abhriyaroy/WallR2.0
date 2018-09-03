package zebrostudio.wallr100.presentation.buypro

import com.uber.autodispose.ScopeProvider
import zebrostudio.wallr100.android.BasePresenter
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity

interface BuyProContract {

  interface BuyProView {
    fun showInvalidPurchaseError()
    fun showUnableToVerifyPurchaseError()
    fun showNoInternetErrorMessage(premiumOperationType: BuyProActivity.PremiumTransactionType)
    fun showGenericVerificationError()
    fun showSuccessfulTransactionMessage(
      proTransactionType: BuyProActivity.PremiumTransactionType
    )
    fun showWaitLoader(proTransactionType: BuyProActivity.PremiumTransactionType)
    fun dismissWaitLoader()
    fun getScope(): ScopeProvider
    fun finishWithResult()

    fun isIabReady(): Boolean
    fun isInternetAvailable(): Boolean
    fun launchPurchase()
    fun launchRestore()

  }

  interface BuyProPresenter : BasePresenter<BuyProView> {

    fun notifyPurchaseClicked()
    fun notifyRestoreClicked()
    fun verifyPurchase(
      packageName: String,
      skuId: String,
      purchaseToken: String,
      proTransactionType: BuyProActivity.PremiumTransactionType
    )
  }

}