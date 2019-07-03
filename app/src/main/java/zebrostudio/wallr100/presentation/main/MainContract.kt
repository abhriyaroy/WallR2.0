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
    fun isOperationActive(): Boolean
    fun showFeedbackClient(
      emailSubject: String,
      emailAddress: Array<String>,
      emailIntentType: String
    )
    fun hideBuyProLayout()
    fun showBuyProLayout()
    fun hideProBadge()
    fun showProBadge()
  }

  interface MainPresenter : BasePresenter<MainView> {
    fun handleViewCreated()
    fun handleViewResumed()
    fun handleViewResult(requestCode: Int, resultCode: Int)
    fun handleBackPress()
    fun handleNavigationMenuOpened()
    fun handleNavigationMenuClosed()
    fun setBackPressedFlagToFalse()
    fun handleHamburgerHintDismissed()
    fun handleFeedbackMenuItemClick()
  }
}