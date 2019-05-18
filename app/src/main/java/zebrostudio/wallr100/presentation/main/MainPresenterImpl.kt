package zebrostudio.wallr100.presentation.main

import zebrostudio.wallr100.android.utils.FragmentTag.COLLECTIONS_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.EXPLORE_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.MINIMAL_TAG
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.main.MainContract.MainPresenter

class MainPresenterImpl(
  private val widgetHintsUseCase: WidgetHintsUseCase,
  private val userPremiumStatusUseCase: UserPremiumStatusUseCase,
  private val collectionImagesUseCase: CollectionImagesUseCase
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
    if (collectionImagesUseCase.wasAutomaticWallpaperChangerEnabled()) {
      collectionImagesUseCase.startAutomaticWallpaperChanger()
    }
  }

  override fun handleBackPress() {
    if (mainView?.isOperationActive() != true) {
      if (isGuillotineMenuOpen) {
        mainView?.closeNavigationMenu()
      } else {
        mainView?.getFragmentTagAtStackTop().let {
          if (it == EXPLORE_TAG) {
            if (backPressedOnce) {
                mainView?.exitApp()
            } else {
              backPressedOnce = true
              mainView?.showExitConfirmation()
              mainView?.startBackPressedFlagResetTimer()
            }
          } else {
            mainView?.showAppBar()
            if ((it == MINIMAL_TAG || it == COLLECTIONS_TAG) && mainView?.isCabActive() == true) {
              mainView?.dismissCab()
            } else {
              mainView?.showPreviousFragment()
            }
          }
        }
      }
    } else {
      mainView?.showOperationInProgressMessage()
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
