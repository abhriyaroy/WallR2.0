package zebrostudio.wallr100.presentation.buypro

import zebrostudio.wallr100.android.BasePresenter

interface BuyProContract {

  interface BuyProView {

    fun showInvalidPurchaseError()
    fun showUnableToVerifyPurchaseError()
    fun showGenericPurchaseVerificationError()

  }

  interface BuyProPresenter : BasePresenter<BuyProView> {

    fun verifyPurchase(packageName: String, skuId: String, purchaseToken: String)

  }

}