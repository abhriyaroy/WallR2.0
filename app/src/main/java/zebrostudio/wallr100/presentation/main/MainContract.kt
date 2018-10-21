package zebrostudio.wallr100.presentation.main

import zebrostudio.wallr100.presentation.BasePresenter

interface MainContract {

  interface MainView {

    fun showExitConfirmation()
    fun exitApp()
    fun closeNavigationMenu()
    fun showPreviousFragment()
    fun getFragmentTagAtStackTop(): String
    fun getExploreFragmentTag(): String
    fun startBackPressedFlagResetTimer()

  }

  interface MainPresenter : BasePresenter<MainView> {

    fun handleBackPress()
    fun notifyNavigationMenuOpened()
    fun notifyNavigationMenuClosed()
    fun setBackPressedFlagToFalse()
    fun shouldShowPurchaseOption(): Boolean

  }
}