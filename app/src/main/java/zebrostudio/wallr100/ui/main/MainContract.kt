package zebrostudio.wallr100.ui.main

import zebrostudio.wallr100.BasePresenter

interface MainContract {

  interface MainView {

    fun showExitConfirmation()
    fun exitApp()
    fun closeNavigationMenu()
    fun showPreviousFragment()
    fun getFragmentTagAtStackTop(): String
  }

  interface MainPresenter : BasePresenter<MainView> {

    fun handleBackPress()
    fun notifyNavigationMenuOpened()
    fun notifyNavigationMenuClosed()

  }
}