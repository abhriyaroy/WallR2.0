package zebrostudio.wallr100.presentation.main

import zebrostudio.wallr100.android.utils.FragmentTag
import zebrostudio.wallr100.presentation.BasePresenter

interface MainContract {

  interface MainView {
    fun showHamburgerHint()
    fun showExitConfirmation()
    fun exitApp()
    fun closeNavigationMenu()
    fun showPreviousFragment()
    fun getFragmentTagAtStackTop(): FragmentTag
    fun startBackPressedFlagResetTimer()
    fun isCabActive(): Boolean
    fun dismissCab()
    fun showAppBar()
    fun showOperationInProgressMessage()
    fun isOperationActive() : Boolean
  }

  interface MainPresenter : BasePresenter<MainView> {
    fun handleViewCreated()
    fun handleBackPress()
    fun handleNavigationMenuOpened()
    fun handleNavigationMenuClosed()
    fun setBackPressedFlagToFalse()
    fun shouldShowPurchaseOption(): Boolean
    fun handleHamburgerHintDismissed()
  }
}