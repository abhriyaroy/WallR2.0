package zebrostudio.wallr100.ui.buypro

import zebrostudio.wallr100.BasePresenter

interface BuyProContract {

  interface BuyProView

  interface BuyProPresenter : BasePresenter<BuyProView>{

    fun notifyBuyProClicked()
    fun notifyRestoreProClicked()

  }

}