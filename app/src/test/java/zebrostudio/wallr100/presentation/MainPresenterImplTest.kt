package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.system.SystemDetailsProvider
import zebrostudio.wallr100.android.utils.FragmentTag.CATEGORIES_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.COLLECTIONS_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.EXPLORE_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.MINIMAL_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.TOP_PICKS_TAG
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.main.MainContract.MainView
import zebrostudio.wallr100.presentation.main.MainPresenterImpl

@RunWith(MockitoJUnitRunner::class)
class MainPresenterImplTest {

  @Mock lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase
  @Mock lateinit var widgetHintsUseCase: WidgetHintsUseCase
  @Mock lateinit var collectionImagesUseCase: CollectionImagesUseCase
  @Mock lateinit var systemDetailsProvider: SystemDetailsProvider
  @Mock lateinit var mainView: MainView
  private lateinit var mainPresenter: MainPresenterImpl

  @Before
  fun setup() {
    mainPresenter =
        MainPresenterImpl(widgetHintsUseCase, userPremiumStatusUseCase, collectionImagesUseCase,
            systemDetailsProvider)
    mainPresenter.attachView(mainView)
  }

  @Test fun `should show hint on handleViewCreated call success and hint is not shown before`() {
    `when`(widgetHintsUseCase.isNavigationMenuHamburgerHintShown()).thenReturn(false)
    `when`(collectionImagesUseCase.wasAutomaticWallpaperChangerEnabled()).thenReturn(false)

    mainPresenter.handleViewCreated()

    verify(widgetHintsUseCase).isNavigationMenuHamburgerHintShown()
    verify(collectionImagesUseCase).wasAutomaticWallpaperChangerEnabled()
    verify(mainView).showHamburgerHint()
  }

  @Test fun `should not show hint on handleViewCreated call success and hint is shown before`() {
    `when`(widgetHintsUseCase.isNavigationMenuHamburgerHintShown()).thenReturn(true)
    `when`(collectionImagesUseCase.wasAutomaticWallpaperChangerEnabled()).thenReturn(false)

    mainPresenter.handleViewCreated()

    verify(widgetHintsUseCase).isNavigationMenuHamburgerHintShown()
    verify(collectionImagesUseCase).wasAutomaticWallpaperChangerEnabled()
  }

  @Test
  fun `should start wallpaper changer and show hint on handleViewCreated call success and hint is not shown before`() {
    `when`(widgetHintsUseCase.isNavigationMenuHamburgerHintShown()).thenReturn(false)
    `when`(collectionImagesUseCase.wasAutomaticWallpaperChangerEnabled()).thenReturn(true)

    mainPresenter.handleViewCreated()

    verify(widgetHintsUseCase).isNavigationMenuHamburgerHintShown()
    verify(collectionImagesUseCase).wasAutomaticWallpaperChangerEnabled()
    verify(collectionImagesUseCase).startAutomaticWallpaperChanger()
    verify(mainView).showHamburgerHint()
  }

  @Test fun `should save hint shown state on handleHamburgerHintDismissed call success`() {
    mainPresenter.handleHamburgerHintDismissed()

    verify(widgetHintsUseCase).saveNavigationMenuHamburgerHintShownState()
  }

  @Test fun `should close guillotine menu on handleBackPress call success`() {
    mainPresenter.isGuillotineMenuOpen = true
    `when`(mainView.isOperationActive()).thenReturn(false)

    mainPresenter.handleBackPress()

    verify(mainView).isOperationActive()
    verify(mainView).closeNavigationMenu()
  }

  @Test
  fun `should block back press on handleBackPress call success with operation still being active`() {
    `when`(mainView.isOperationActive()).thenReturn(true)

    mainPresenter.handleBackPress()

    verify(mainView).isOperationActive()
    verify(mainView).showOperationInProgressMessage()
  }

  @Test
  fun `should show previous fragment on handleBackPress call success with top picks fragment on stack top`() {
    mainPresenter.isGuillotineMenuOpen = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(TOP_PICKS_TAG)
    `when`(mainView.isOperationActive()).thenReturn(false)

    mainPresenter.handleBackPress()

    verify(mainView).isOperationActive()
    verify(mainView).getFragmentTagAtStackTop()
    verify(mainView).showAppBar()
    verify(mainView).showPreviousFragment()
  }

  @Test
  fun `should show previous fragment on handleBackPress call success with categories fragment on stack top`() {
    mainPresenter.isGuillotineMenuOpen = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(CATEGORIES_TAG)
    `when`(mainView.isOperationActive()).thenReturn(false)

    mainPresenter.handleBackPress()

    verify(mainView).isOperationActive()
    verify(mainView).getFragmentTagAtStackTop()
    verify(mainView).showAppBar()
    verify(mainView).showPreviousFragment()
  }

  @Test
  fun `should dismiss cab on handleBackPress call success with minimal fragment on stack top and cab in active state`() {
    mainPresenter.isGuillotineMenuOpen = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(MINIMAL_TAG)
    `when`(mainView.isCabActive()).thenReturn(true)
    `when`(mainView.isOperationActive()).thenReturn(false)

    mainPresenter.handleBackPress()

    verify(mainView).isOperationActive()
    verify(mainView).getFragmentTagAtStackTop()
    verify(mainView).showAppBar()
    verify(mainView).isCabActive()
    verify(mainView).dismissCab()
  }

  @Test
  fun `should show previous fragment on handleBackPress call success with minimal fragment on stack top`() {
    mainPresenter.isGuillotineMenuOpen = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(MINIMAL_TAG)
    `when`(mainView.isCabActive()).thenReturn(false)
    `when`(mainView.isOperationActive()).thenReturn(false)

    mainPresenter.handleBackPress()

    verify(mainView).isOperationActive()
    verify(mainView).getFragmentTagAtStackTop()
    verify(mainView).showAppBar()
    verify(mainView).isCabActive()
    verify(mainView).showPreviousFragment()
  }

  @Test
  fun `should show previous fragment on handleBackPress call success with collections fragment on stack top`() {
    mainPresenter.isGuillotineMenuOpen = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(COLLECTIONS_TAG)
    `when`(mainView.isOperationActive()).thenReturn(false)

    mainPresenter.handleBackPress()

    verify(mainView).isOperationActive()
    verify(mainView).getFragmentTagAtStackTop()
    verify(mainView).isCabActive()
    verify(mainView).showAppBar()
    verify(mainView).showPreviousFragment()
  }

  @Test
  fun `should show exit confirmation message on handleBackPress call success with explore fragment on stack top`() {
    mainPresenter.isGuillotineMenuOpen = false
    mainPresenter.backPressedOnce = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(EXPLORE_TAG)
    `when`(mainView.isOperationActive()).thenReturn(false)

    mainPresenter.handleBackPress()

    verify(mainView).isOperationActive()
    verify(mainView).getFragmentTagAtStackTop()
    verify(mainView).showExitConfirmation()
    verify(mainView).startBackPressedFlagResetTimer()
  }

  @Test
  fun `should exit app on handleBackPress call success with explore fragment on stack top and back was already pressed once before`() {
    mainPresenter.isGuillotineMenuOpen = false
    mainPresenter.backPressedOnce = true
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(EXPLORE_TAG)
    `when`(mainView.isOperationActive()).thenReturn(false)

    mainPresenter.handleBackPress()

    verify(mainView).isOperationActive()
    verify(mainView).getFragmentTagAtStackTop()
    verify(mainView).exitApp()
  }

  @Test
  fun `should set isGuillotineMenu open to true on handleNavigationMenuOpened call success`() {
    mainPresenter.handleNavigationMenuOpened()

    assertTrue(mainPresenter.isGuillotineMenuOpen)
  }

  @Test
  fun `should set isGuillotineMenu open to false on handleNavigationMenuClosed call success`() {
    mainPresenter.handleNavigationMenuClosed()

    assertFalse(mainPresenter.isGuillotineMenuOpen)
  }

  @Test
  fun `should set backPressedOnce to false on setBackPressedFlagToFalse call success`() {
    mainPresenter.setBackPressedFlagToFalse()

    assertFalse(mainPresenter.isGuillotineMenuOpen)
  }

  @Test fun `should return true on shouldShowPurchaseOption call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    assertTrue(mainPresenter.shouldShowPurchaseOption())
    verify(userPremiumStatusUseCase).isUserPremium()
  }

  @Test fun `should show feedback client on handleFeedbackMenuItemClick call success`() {
    val osVersion = "os_version"
    val buildNumber = "build_number"
    val sdkVersion = "sdk_version"
    val deviceName = "device_name"
    val modelName = "model_name"
    val productName = "product_name"
    val messageSubject =
        "Feedback/Report - WallR -> Debug-infos:\n OS Version: $osVersion" +
            " ($buildNumber)\n OS API Level: " +
            "$sdkVersion\n Device: $deviceName\n Model(and Product): $modelName ($productName)"
    val emailAddress = "studio.zebro@gmail.com"
    val contentType = "text/plain"
    `when`(systemDetailsProvider.getOsVersion()).thenReturn(osVersion)
    `when`(systemDetailsProvider.getBuildNumber()).thenReturn(buildNumber)
    `when`(systemDetailsProvider.getSdkVersion()).thenReturn(sdkVersion)
    `when`(systemDetailsProvider.getDeviceName()).thenReturn(deviceName)
    `when`(systemDetailsProvider.getModelName()).thenReturn(modelName)
    `when`(systemDetailsProvider.getProductName()).thenReturn(productName)

    mainPresenter.handleFeedbackMenuItemClick()

    verify(systemDetailsProvider).getOsVersion()
    verify(systemDetailsProvider).getBuildNumber()
    verify(systemDetailsProvider).getSdkVersion()
    verify(systemDetailsProvider).getDeviceName()
    verify(systemDetailsProvider).getModelName()
    verify(systemDetailsProvider).getProductName()
    verify(mainView).showFeedbackClient(messageSubject, arrayOf(emailAddress), contentType)
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(userPremiumStatusUseCase, widgetHintsUseCase, collectionImagesUseCase,
        mainView, systemDetailsProvider)
    mainPresenter.detachView()
  }

}