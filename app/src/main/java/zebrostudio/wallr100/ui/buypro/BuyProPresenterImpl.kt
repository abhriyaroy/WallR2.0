package zebrostudio.wallr100.ui.buypro

class BuyProPresenterImpl : BuyProContract.BuyProPresenter {
  private var buyProView: BuyProContract.BuyProView? = null

  override fun attachView(view: BuyProContract.BuyProView) {
    buyProView = view
  }

  override fun detachView() {
    buyProView = null
  }

  override fun notifyBuyProClicked() {

  }

  override fun notifyRestoreProClicked() {

  }

}