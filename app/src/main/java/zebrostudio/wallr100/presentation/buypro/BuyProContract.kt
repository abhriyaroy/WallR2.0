package zebrostudio.wallr100.presentation.buypro

import com.uber.autodispose.ScopeProvider
import zebrostudio.wallr100.android.BasePresenter
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity

interface BuyProContract {

  interface BuyProView {
    fun showInvalidPurchaseError()
    fun showUnableToVerifyPurchaseError()
    fun showGenericVerificationError()
    fun showSuccessfulTransactionMessage(
      proTransactionType: BuyProActivity.Companion.ProTransactionType
    )
    fun dismissWaitLoader()
    fun getScope() : ScopeProvider
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