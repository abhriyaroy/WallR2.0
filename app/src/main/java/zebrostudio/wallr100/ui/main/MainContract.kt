package zebrostudio.wallr100.ui.main

import zebrostudio.wallr100.BasePresenter

interface MainContract {

  interface MainView {

    fun showExitConfirmation()
    fun exitApp()
    fun closeGuillotineMenu()
    fun showPreviousFragment()

  }

  interface MainPresenter : BasePresenter<MainView> {

    fun handleBackPress()
    fun notifyNavigationMenuOpened()
    fun notifyNavigationMenuClosed()

  }
}