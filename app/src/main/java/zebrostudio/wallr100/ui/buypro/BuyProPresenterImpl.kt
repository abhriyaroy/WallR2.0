package zebrostudio.wallr100.ui.buypro

import zebrostudio.wallr100.data.DataRepository

class BuyProPresenterImpl(private var dataRepository: DataRepository) : BuyProContract.BuyProPresenter {

  private var buyProView: BuyProContract.BuyProView? = null

  override fun attachView(view: BuyProContract.BuyProView) {
    buyProView = view
    dataRepository.initPurchaseHelper()
  }

  override fun detachView() {
    dataRepository.disposePurchaseHelper()
    buyProView = null
  }

  override fun notifyBuyProClicked() {

  }

  override fun notifyRestoreProClicked() {

  }

}