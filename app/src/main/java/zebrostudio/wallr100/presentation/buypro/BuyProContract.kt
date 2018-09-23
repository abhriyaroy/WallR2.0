package zebrostudio.wallr100.presentation.buypro

import com.uber.autodispose.ScopeProvider
import zebrostudio.wallr100.android.BasePresenter
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.buypro.PremiumTransactionType

interface BuyProContract {

  interface BuyProView {
    fun showInvalidPurchaseError()
    fun showUnableToVerifyPurchaseError()
    fun showNoInternetErrorMessage(premiumOperationType: PremiumTransactionType)
    fun showGenericVerificationError()
    fun showSuccessfulTransactionMessage(
      proTransactionType: PremiumTransactionType
    )

    fun showWaitLoader(proTransactionType: PremiumTransactionType)
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
      proTransactionType: PremiumTransactionType
    )
  }

}