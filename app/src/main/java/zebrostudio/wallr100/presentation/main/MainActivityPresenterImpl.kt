package zebrostudio.wallr100.presentation.main

import android.os.Handler
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment

class MainActivityPresenterImpl : MainContract.MainPresenter {

  private var backPressedOnce = false
  private var isGuillotineMenuOpen = false

  private var mainView: MainContract.MainView? = null

  override fun attachView(view: MainContract.MainView) {
    mainView = view
  }

  override fun detachView() {
    mainView = null
  }

  override fun handleBackPress() {
    if (isGuillotineMenuOpen) {
      mainView?.closeNavigationMenu()
    } else {
      if (mainView?.getFragmentTagAtStackTop() == WallpaperFragment.EXPLORE_FRAGMENT_TAG) {
        if (backPressedOnce) {
          mainView?.exitApp()
        } else {
          backPressedOnce = true
          mainView?.showExitConfirmation()
          Handler().postDelayed({ backPressedOnce = false }, 2000)
        }
      } else {
        mainView?.showPreviousFragment()
      }
    }
  }

  override fun notifyNavigationMenuOpened() {
    isGuillotineMenuOpen = true
  }

  override fun notifyNavigationMenuClosed() {
    isGuillotineMenuOpen = false
  }

}
