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
    fun getMinimalFragmentTag(): String
    fun startBackPressedFlagResetTimer()
    fun isCabActive(): Boolean
    fun dismissCab()
    fun showAppBar()
  }

  interface MainPresenter : BasePresenter<MainView> {
    fun handleBackPress()
    fun handleNavigationMenuOpened()
    fun handleNavigationMenuClosed()
    fun setBackPressedFlagToFalse()
    fun shouldShowPurchaseOption(): Boolean
  }
}