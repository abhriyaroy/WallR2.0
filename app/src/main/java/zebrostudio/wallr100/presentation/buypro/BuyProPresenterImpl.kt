package zebrostudio.wallr100.presentation.buypro

class BuyProPresenterImpl : BuyProContract.BuyProPresenter {

  private var buyProView: BuyProContract.BuyProView? = null

  override fun attachView(view: BuyProContract.BuyProView) {
    buyProView = view
  }

  override fun detachView() {
    buyProView = null
  }

}