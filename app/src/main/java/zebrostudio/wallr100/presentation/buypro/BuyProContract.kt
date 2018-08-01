package zebrostudio.wallr100.presentation.buypro

import zebrostudio.wallr100.android.BasePresenter
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity

interface BuyProContract {

  interface BuyProView {
    fun showWaitLoader(materialLoaderType: BuyProActivity.Companion.MaterialLoaderType)
    fun showNoInternetErrorMessage()
    fun showInvalidPurchaseError()
    fun showUnableToVerifyPurchaseError()
    fun showGenericPurchaseVerificationError()

    fun dismissWaitLoader()
  }

  interface BuyProPresenter : BasePresenter<BuyProView> {
    fun purchaseButtonClicked()
    fun restoreButtonClicked()
    fun verifyPurchase(packageName: String, skuId: String, purchaseToken: String)
  }

}