package zebrostudio.wallr100.presentation.main

import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.main.MainContract.MainPresenter

class MainPresenterImpl(
  private val widgetHintsUseCase: WidgetHintsUseCase,
  private val userPremiumStatusUseCase: UserPremiumStatusUseCase
) : MainPresenter {

  internal var backPressedOnce = false
  internal var isGuillotineMenuOpen = false
  private var mainView: MainContract.MainView? = null

  override fun attachView(view: MainContract.MainView) {
    mainView = view
  }

  override fun detachView() {
    mainView = null
  }

  override fun handleViewCreated() {
    if (!widgetHintsUseCase.isNavigationMenuHamburgerHintShown()) {
      mainView?.showHamburgerHint()
    }
  }

  override fun handleBackPress() {
    if (isGuillotineMenuOpen) {
      mainView?.closeNavigationMenu()
    } else {
      if (mainView?.getFragmentTagAtStackTop() == mainView?.getExploreFragmentTag()) {
        if (backPressedOnce) {
          mainView?.exitApp()
        } else {
          backPressedOnce = true
          mainView?.showExitConfirmation()
          mainView?.startBackPressedFlagResetTimer()
        }
      } else {
        mainView?.showAppBar()
        if (mainView?.getFragmentTagAtStackTop() == mainView?.getMinimalFragmentTag() &&
            mainView?.isCabActive() == true) {
          mainView?.dismissCab()
        } else {
          mainView?.showPreviousFragment()
        }
      }
    }
  }

  override fun handleNavigationMenuOpened() {
    isGuillotineMenuOpen = true
  }

  override fun handleNavigationMenuClosed() {
    isGuillotineMenuOpen = false
  }

  override fun setBackPressedFlagToFalse() {
    backPressedOnce = false
  }

  override fun shouldShowPurchaseOption() = !userPremiumStatusUseCase.isUserPremium()

  override fun handleHamburgerHintDismissed() {
    widgetHintsUseCase.saveNavigationMenuHamburgerHintShownState()
  }

}
