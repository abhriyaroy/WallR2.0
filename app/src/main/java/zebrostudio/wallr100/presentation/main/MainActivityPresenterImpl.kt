package zebrostudio.wallr100.presentation.main

import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase

class MainActivityPresenterImpl(
  private var userPremiumStatusUseCase: UserPremiumStatusUseCase
) : MainContract.MainPresenter {

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
      if (mainView?.getFragmentTagAtStackTop() == mainView?.getExploreFragmentTag()) {
        if (backPressedOnce) {
          mainView?.exitApp()
        } else {
          backPressedOnce = true
          mainView?.showExitConfirmation()
          mainView?.startBackPressedFlagResetTimer()
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

  override fun setBackPressedFlagToFalse() {
    backPressedOnce = false
  }

  override fun shouldShowPurchaseOption(): Boolean {
    return !userPremiumStatusUseCase.isUserPremium()
  }

}
