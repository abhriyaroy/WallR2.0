package zebrostudio.wallr100.ui.main

import android.os.Handler

class MainActivityPresenterImpl : MainContract.MainPresenter {

  var backPressedOnce: Boolean = false
  private var mainView: MainContract.MainView? = null

  override fun attach(view: MainContract.MainView) {
    mainView = view
  }

  override fun detach() {
    mainView = null
  }

  override fun handleBackPress() {
    if (backPressedOnce) {
      mainView?.exitApp()
    } else {
      backPressedOnce = true
      mainView?.showExitToast()
      Handler().postDelayed({ backPressedOnce = false }, 2000)
    }
  }

}