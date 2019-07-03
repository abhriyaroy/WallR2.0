package zebrostudio.wallr100.domain

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.interactor.WidgetHintsInteractor

@RunWith(MockitoJUnitRunner::class)
class WidgetHintsUseCaseTest {

  @Mock
  private lateinit var wallrRepository: WallrRepository
  private lateinit var widgetHintsInteractor: WidgetHintsInteractor

  @Before
  fun setup() {
    widgetHintsInteractor = WidgetHintsInteractor(wallrRepository)
  }

  @Test
  fun `should return false on isNavigationMenuHamburgerHintShown call success`() {
    `when`(wallrRepository.wasAppOpenedBefore()).thenReturn(false)

    assertEquals(false, widgetHintsInteractor.isNavigationMenuHamburgerHintShown())
    verify(wallrRepository).wasAppOpenedBefore()
  }

  @Test
  fun `should return true on isNavigationMenuHamburgerHintShown call success`() {
    `when`(wallrRepository.wasAppOpenedBefore()).thenReturn(true)

    assertEquals(true, widgetHintsInteractor.isNavigationMenuHamburgerHintShown())
    verify(wallrRepository).wasAppOpenedBefore()
  }

  @Test
  fun `should save app opened previous state on saveNavigationMenuHamburgerHintShownState call success`() {
    widgetHintsInteractor.saveNavigationMenuHamburgerHintShownState()

    verify(wallrRepository).saveAppPreviouslyOpenedState()
  }

  @Test
  fun `should return false on isMultiColorImageModesHintShown call success`() {
    `when`(wallrRepository.isMultiColorModesHintShown()).thenReturn(false)

    assertEquals(false, widgetHintsInteractor.isMultiColorImageModesHintShown())
    verify(wallrRepository).isMultiColorModesHintShown()
  }

  @Test
  fun `should return true on isMultiColorImageModesHintShown call success`() {
    `when`(wallrRepository.isMultiColorModesHintShown()).thenReturn(true)

    assertEquals(true, widgetHintsInteractor.isMultiColorImageModesHintShown())
    verify(wallrRepository).isMultiColorModesHintShown()
  }

  @Test
  fun `should save multicolor image hint shown state on saveMultiColorImageHintShownState call success`() {
    widgetHintsInteractor.saveMultiColorImageHintShownState()

    verify(wallrRepository).saveMultiColorModesHintShownState()
  }

  @Test
  fun `should return false on isCollectionsImageReorderHintShown call success`() {
    `when`(wallrRepository.isCollectionReorderHintDisplayedBefore()).thenReturn(false)

    assertEquals(false, widgetHintsInteractor.isCollectionsImageReorderHintShown())
    verify(wallrRepository).isCollectionReorderHintDisplayedBefore()
  }

  @Test
  fun `should return true on isCollectionsImageReorderHintShown call success`() {
    `when`(wallrRepository.isCollectionReorderHintDisplayedBefore()).thenReturn(true)

    assertEquals(true, widgetHintsInteractor.isCollectionsImageReorderHintShown())
    verify(wallrRepository).isCollectionReorderHintDisplayedBefore()
  }

  @Test
  fun `should save collections image reorder hint shown state on saveCollectionsImageReorderHintShown call success`() {
    widgetHintsInteractor.saveCollectionsImageReorderHintShown()

    verify(wallrRepository).saveCollectionReorderHintShownState()
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(wallrRepository)
  }

}