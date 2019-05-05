package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.times
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
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.main.MainContract.MainView
import zebrostudio.wallr100.presentation.main.MainPresenterImpl

const val EXPLORE_TAG = "Explore"
const val TOP_PICKS_TAG = "Top Picks"
const val CATEGORIES_TAG = "Categories"
const val MINIMAL_TAG = "Minimal"
const val COLLECTIONS_TAG = "Collections"

@RunWith(MockitoJUnitRunner::class)
class MainPresenterImplTest {

  @Mock lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase
  @Mock lateinit var mainView: MainView
  private lateinit var mainPresenter: MainPresenterImpl

  @Before
  fun setup() {
    mainPresenter = MainPresenterImpl(userPremiumStatusUseCase)
    mainPresenter.attachView(mainView)
  }

  @Test fun `should close guillotine menu on handleBackPress call success`() {
    mainPresenter.isGuillotineMenuOpen = true

    mainPresenter.handleBackPress()

    verify(mainView).closeNavigationMenu()
  }

  @Test
  fun `should show previous fragment on handleBackPress call success with top picks fragment on stack top`() {
    mainPresenter.isGuillotineMenuOpen = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(TOP_PICKS_TAG)
    `when`(mainView.getExploreFragmentTag()).thenReturn(EXPLORE_TAG)
    `when`(mainView.getMinimalFragmentTag()).thenReturn(MINIMAL_TAG)

    mainPresenter.handleBackPress()

    verify(mainView, times(2)).getFragmentTagAtStackTop()
    verify(mainView).getExploreFragmentTag()
    verify(mainView).getMinimalFragmentTag()
    verify(mainView).showAppBar()
    verify(mainView).showPreviousFragment()
  }

  @Test
  fun `should show previous fragment on handleBackPress call success with categories fragment on stack top`() {
    mainPresenter.isGuillotineMenuOpen = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(CATEGORIES_TAG)
    `when`(mainView.getExploreFragmentTag()).thenReturn(EXPLORE_TAG)
    `when`(mainView.getMinimalFragmentTag()).thenReturn(MINIMAL_TAG)

    mainPresenter.handleBackPress()

    verify(mainView, times(2)).getFragmentTagAtStackTop()
    verify(mainView).getExploreFragmentTag()
    verify(mainView).getMinimalFragmentTag()
    verify(mainView).showAppBar()
    verify(mainView).showPreviousFragment()
  }

  @Test
  fun `should dismiss cab on handleBackPress call success with minimal fragment on stack top and cab in active state`() {
    mainPresenter.isGuillotineMenuOpen = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(MINIMAL_TAG)
    `when`(mainView.getExploreFragmentTag()).thenReturn(EXPLORE_TAG)
    `when`(mainView.getMinimalFragmentTag()).thenReturn(MINIMAL_TAG)
    `when`(mainView.isCabActive()).thenReturn(true)

    mainPresenter.handleBackPress()

    verify(mainView, times(2)).getFragmentTagAtStackTop()
    verify(mainView).getExploreFragmentTag()
    verify(mainView).getMinimalFragmentTag()
    verify(mainView).showAppBar()
    verify(mainView).isCabActive()
    verify(mainView).dismissCab()
  }

  @Test
  fun `should show previous fragment on handleBackPress call success with minimal fragment on stack top`() {
    mainPresenter.isGuillotineMenuOpen = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(MINIMAL_TAG)
    `when`(mainView.getExploreFragmentTag()).thenReturn(EXPLORE_TAG)
    `when`(mainView.getMinimalFragmentTag()).thenReturn(MINIMAL_TAG)
    `when`(mainView.isCabActive()).thenReturn(false)

    mainPresenter.handleBackPress()

    verify(mainView, times(2)).getFragmentTagAtStackTop()
    verify(mainView).getExploreFragmentTag()
    verify(mainView).getMinimalFragmentTag()
    verify(mainView).showAppBar()
    verify(mainView).isCabActive()
    verify(mainView).showPreviousFragment()
  }

  @Test
  fun `should show previous fragment on handleBackPress call success with collections fragment on stack top`() {
    mainPresenter.isGuillotineMenuOpen = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(COLLECTIONS_TAG)
    `when`(mainView.getExploreFragmentTag()).thenReturn(EXPLORE_TAG)
    `when`(mainView.getMinimalFragmentTag()).thenReturn(MINIMAL_TAG)

    mainPresenter.handleBackPress()

    verify(mainView, times(2)).getFragmentTagAtStackTop()
    verify(mainView).getExploreFragmentTag()
    verify(mainView).getMinimalFragmentTag()
    verify(mainView).showAppBar()
    verify(mainView).showPreviousFragment()
  }

  @Test
  fun `should show exit confirmation message on handleBackPress call success with explore fragment on stack top`() {
    mainPresenter.isGuillotineMenuOpen = false
    mainPresenter.backPressedOnce = false
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(EXPLORE_TAG)
    `when`(mainView.getExploreFragmentTag()).thenReturn(EXPLORE_TAG)

    mainPresenter.handleBackPress()

    verify(mainView).getFragmentTagAtStackTop()
    verify(mainView).getExploreFragmentTag()
    verify(mainView).showExitConfirmation()
    verify(mainView).startBackPressedFlagResetTimer()
  }

  @Test
  fun `should exit app on handleBackPress call success with explore fragment on stack top and back was already pressed once before`() {
    mainPresenter.isGuillotineMenuOpen = false
    mainPresenter.backPressedOnce = true
    `when`(mainView.getFragmentTagAtStackTop()).thenReturn(EXPLORE_TAG)
    `when`(mainView.getExploreFragmentTag()).thenReturn(EXPLORE_TAG)

    mainPresenter.handleBackPress()

    verify(mainView).getFragmentTagAtStackTop()
    verify(mainView).getExploreFragmentTag()
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

  @After
  fun tearDown() {
    verifyNoMoreInteractions(userPremiumStatusUseCase, mainView)
    mainPresenter.detachView()
  }

}