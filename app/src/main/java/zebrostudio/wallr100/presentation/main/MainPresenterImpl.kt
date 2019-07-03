package zebrostudio.wallr100.presentation.main

import zebrostudio.wallr100.android.system.SystemInfoProvider
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig
import zebrostudio.wallr100.android.utils.FragmentTag.*
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.main.MainContract.MainPresenter

const val FEEDBACK_CONTENT_TYPE = "text/plain"
const val ZEBRO_STUDIO_EMAIL_ADDRESS = "studio.zebro@gmail.com"

class MainPresenterImpl(
  private val widgetHintsUseCase: WidgetHintsUseCase,
  private val userPremiumStatusUseCase: UserPremiumStatusUseCase,
  private val collectionImagesUseCase: CollectionImagesUseCase,
  private val systemInfoProvider: SystemInfoProvider
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
    if (userPremiumStatusUseCase.isUserPremium()) {
      mainView?.hideBuyProLayout()
      mainView?.showProBadge()
    } else {
      mainView?.showBuyProLayout()
      mainView?.hideProBadge()
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

  override fun handleHamburgerHintDismissed() {
    widgetHintsUseCase.saveNavigationMenuHamburgerHintShownState()
  }

  override fun handleFeedbackMenuItemClick() {
    ("Feedback/Report - WallR -> Debug-infos:\n OS Version: ${systemInfoProvider.getOsVersion()}" +
        " (${systemInfoProvider.getBuildNumber()})\n OS API Level: " +
        "${systemInfoProvider.getSdkVersion()}\n Device: ${systemInfoProvider.getDeviceName()}" +
        "\n Model(and Product): ${systemInfoProvider.getModelName()} (${systemInfoProvider.getProductName()})")
        .let {
          mainView?.showFeedbackClient(it, arrayOf(ZEBRO_STUDIO_EMAIL_ADDRESS),
            FEEDBACK_CONTENT_TYPE)
        }
  }

  override fun handleViewResumed() {
    if (userPremiumStatusUseCase.isUserPremium()) {
      mainView?.hideBuyProLayout()
    }
  }

  override fun handleViewResult(requestCode: Int, resultCode: Int) {
    if (requestCode == PurchaseTransactionConfig.PURCHASE_REQUEST_CODE &&
        resultCode == PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE) {
      mainView?.hideBuyProLayout()
    }
  }

}
