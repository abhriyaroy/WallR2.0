package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider.TestLifecycle.STARTED
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider.createInitial
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.exception.UnableToGetMinimalColorsException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.MinimalImagesUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.datafactory.RestoreColorsModelFactory
import zebrostudio.wallr100.presentation.minimal.INITIAL_OFFSET
import zebrostudio.wallr100.presentation.minimal.INITIAL_SIZE
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalView
import zebrostudio.wallr100.presentation.minimal.MinimalPresenterImpl
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.GRADIENT
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.MATERIAL
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.PLASMA
import zebrostudio.wallr100.presentation.minimal.mapper.RestoreColorsPresenterEntityMapper
import java.util.Collections
import java.util.TreeMap
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class MinimalPresenterImplTest {

  @Mock private lateinit var minimalImagesUseCase: MinimalImagesUseCase
  @Mock lateinit var widgetHintsUseCase: WidgetHintsUseCase
  @Mock private lateinit var postExecutionThread: PostExecutionThread
  @Mock private lateinit var minimalView: MinimalView
  private lateinit var minimalPresenter: MinimalPresenterImpl
  private var randomString = randomUUID().toString()

  @Before fun setup() {
    minimalPresenter =
        MinimalPresenterImpl(widgetHintsUseCase, minimalImagesUseCase, postExecutionThread)
    minimalPresenter.attachView(minimalView)

    `when`(minimalView.getScope()).thenReturn(createInitial(STARTED))
    `when`(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  @Test
  fun `should display default colors on handleViewCreated call success as custom color list not present`() {
    val list = listOf(randomString)
    `when`(minimalImagesUseCase.isCustomColorListPresent()).thenReturn(false)
    `when`(minimalImagesUseCase.getDefaultColors()).thenReturn(Single.just(list))

    minimalPresenter.handleViewCreated()

    verify(minimalImagesUseCase).isCustomColorListPresent()
    verify(minimalImagesUseCase).getDefaultColors()
    verify(minimalView).setColorList(list)
    verify(minimalView).getScope()
    verify(minimalView).updateAllItems()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should call showUnableToGetColorsErrorMessage on handleViewCreated call failure as custom color list is not present`() {
    `when`(minimalImagesUseCase.isCustomColorListPresent()).thenReturn(false)
    `when`(minimalImagesUseCase.getDefaultColors()).thenReturn(
        Single.error(UnableToGetMinimalColorsException()))

    minimalPresenter.handleViewCreated()

    verify(minimalImagesUseCase).isCustomColorListPresent()
    verify(minimalImagesUseCase).getDefaultColors()
    verify(minimalView).showUnableToGetColorsErrorMessage()
    verify(minimalView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should call showGenericErrorMessage on handleViewCreated call failure as custom color list is not present`() {
    `when`(minimalImagesUseCase.isCustomColorListPresent()).thenReturn(false)
    `when`(minimalImagesUseCase.getDefaultColors()).thenReturn(Single.error(Exception()))

    minimalPresenter.handleViewCreated()

    verify(minimalImagesUseCase).isCustomColorListPresent()
    verify(minimalImagesUseCase).getDefaultColors()
    verify(minimalImagesUseCase).isCustomColorListPresent()
    verify(minimalImagesUseCase).getDefaultColors()
    verify(minimalView).showGenericErrorMessage()
    verify(minimalView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should display custom colors on handleViewCreated call success when custom color is list present`() {
    val list = listOf(randomString)
    `when`(minimalImagesUseCase.isCustomColorListPresent()).thenReturn(true)
    `when`(minimalImagesUseCase.getCustomColors()).thenReturn(Single.just(list))

    minimalPresenter.handleViewCreated()

    verify(minimalImagesUseCase).isCustomColorListPresent()
    verify(minimalImagesUseCase).getCustomColors()
    verify(minimalView).setColorList(list)
    verify(minimalView).getScope()
    verify(minimalView).updateAllItems()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should call showUnableToGetColorsErrorMessage on handleViewCreated call failure even though custom color list is present`() {
    `when`(minimalImagesUseCase.isCustomColorListPresent()).thenReturn(true)
    `when`(minimalImagesUseCase.getCustomColors()).thenReturn(
        Single.error(UnableToGetMinimalColorsException()))

    minimalPresenter.handleViewCreated()

    verify(minimalImagesUseCase).isCustomColorListPresent()
    verify(minimalImagesUseCase).getCustomColors()
    verify(minimalView).showUnableToGetColorsErrorMessage()
    verify(minimalView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should call showGenericErrorMessage on handleViewCreated call failure and custom color list present`() {
    `when`(minimalImagesUseCase.isCustomColorListPresent()).thenReturn(true)
    `when`(minimalImagesUseCase.getCustomColors()).thenReturn(
        Single.error(Exception()))

    minimalPresenter.handleViewCreated()

    verify(minimalImagesUseCase).isCustomColorListPresent()
    verify(minimalImagesUseCase).getCustomColors()
    verify(minimalView).showGenericErrorMessage()
    verify(minimalView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should hide bottom panel on handleOnScrolled call success and scrollDistance is positive`() {
    val scrollDistance = 50
    minimalPresenter.isBottomPanelEnabled = true

    minimalPresenter.handleOnScrolled(scrollDistance)

    assertFalse(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).hideBottomLayoutWithAnimation()
  }

  @Test
  fun `should show bottom panel on handleOnScrolled call success and scrollDistance is negative and more than 1 item is selected`() {
    val scrollDistance = -50
    minimalPresenter.isBottomPanelEnabled = false
    minimalPresenter.selectionSize = 2

    minimalPresenter.handleOnScrolled(scrollDistance)

    assertTrue(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).showBottomPanelWithAnimation()
  }

  @Test
  fun `should delete items on handleDeleteMenu call success and remaining items size greater than minimum size`() {
    val list = mutableListOf<String>()
    for (i in 1..25) {
      list.add(randomString)
    }
    val modifiedList = mutableListOf<String>()
    for (i in 1..20) {
      modifiedList.add(randomString)
    }
    val map = HashMap<Int, String>()
    map[5] = randomString
    map[0] = randomString
    map[9] = randomString
    map[6] = randomString
    val sortedMap = TreeMap<Int, String>(Collections.reverseOrder())
    sortedMap.putAll(map)
    val inOrder = inOrder(minimalView)
    `when`(minimalImagesUseCase.modifyColors(list, map)).thenReturn(Single.just(modifiedList))

    minimalPresenter.handleDeleteMenuItemClick(list, map)

    assertFalse(minimalPresenter.shouldUpdateAllItems)
    verify(minimalImagesUseCase).modifyColors(list, map)
    verify(minimalView).clearSelectedItemsMap()
    verify(minimalView).setColorList(modifiedList)
    verify(minimalView).getScope()
    sortedMap.keys.forEach {
      inOrder.verify(minimalView).removeItemView(it + INITIAL_OFFSET)
    }
    verify(minimalView).showUndoDeletionOption(map.size)
    verify(minimalView).clearCabIfActive()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should call showDeleteColorsErrorMessage on handleDeleteMenuItemClick call failure`() {
    val list = mutableListOf<String>()
    for (i in 1..25) {
      list.add(randomString)
    }
    val map = hashMapOf(Pair(0, randomString))
    `when`(minimalImagesUseCase.modifyColors(list, map)).thenReturn(Single.error(Exception()))

    minimalPresenter.handleDeleteMenuItemClick(list, map)

    assertTrue(minimalPresenter.shouldUpdateAllItems)
    verify(minimalImagesUseCase).modifyColors(list, map)
    verify(minimalView).getScope()
    verify(minimalView).clearCabIfActive()
    verify(minimalView).showDeleteColorsErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should call showDeselectBeforeDeletionMessage on handleDeleteMenuItemClick call failure as remaining items size less than minimum size`() {
    val list = mutableListOf<String>()
    for (i in 1..20) {
      list.add(randomString)
    }
    val map = hashMapOf(Pair(0, randomString))

    minimalPresenter.handleDeleteMenuItemClick(list, map)

    verify(minimalView).showDeselectBeforeDeletionMessage(map.size)
  }

  @Test
  fun `should call hideBottomPanelWithAnimationInView and updateAllItems on handleCabDestroyed call success`() {
    minimalPresenter.isBottomPanelEnabled = true
    minimalPresenter.shouldUpdateAllItems = true

    minimalPresenter.handleCabDestroyed()

    assertFalse(minimalPresenter.isBottomPanelEnabled)
    assertTrue(minimalPresenter.shouldUpdateAllItems)
    assertEquals(INITIAL_SIZE, minimalPresenter.selectionSize)
    verify(minimalView).clearSelectedItemsMap()
    verify(minimalView).updateAllItems()
    verify(minimalView).hideBottomLayoutWithAnimation()
  }

  @Test
  fun `should call hideBottomPanelWithAnimationInView on handleCabDestroyed call success`() {
    minimalPresenter.isBottomPanelEnabled = true
    minimalPresenter.shouldUpdateAllItems = false

    minimalPresenter.handleCabDestroyed()

    assertFalse(minimalPresenter.isBottomPanelEnabled)
    assertTrue(minimalPresenter.shouldUpdateAllItems)
    assertEquals(INITIAL_SIZE, minimalPresenter.selectionSize)
    verify(minimalView).clearSelectedItemsMap()
    verify(minimalView).hideBottomLayoutWithAnimation()
  }

  @Test
  fun `should call clearSelectedMap on handleCabDestroyed call success`() {
    minimalPresenter.shouldUpdateAllItems = false

    minimalPresenter.handleCabDestroyed()

    assertTrue(minimalPresenter.shouldUpdateAllItems)
    verify(minimalView).clearSelectedItemsMap()
  }

  @Test
  fun `should set multiColorImageType to material on handleSpinnerOptionChanged call success`() {
    minimalPresenter.handleSpinnerOptionChanged(MATERIAL.ordinal)

    assertEquals(MATERIAL, minimalPresenter.multiColorImageType)
  }

  @Test
  fun `should set multiColorImageType to gradient on handleSpinnerOptionChanged call success`() {
    minimalPresenter.handleSpinnerOptionChanged(GRADIENT.ordinal)

    assertEquals(GRADIENT, minimalPresenter.multiColorImageType)
  }

  @Test
  fun `should set multiColorImageType to plasma on handleSpinnerOptionChanged call success`() {
    minimalPresenter.handleSpinnerOptionChanged(PLASMA.ordinal)

    assertEquals(PLASMA, minimalPresenter.multiColorImageType)
  }

  @Test fun `should add color on handleColorPickerPositiveClick call success`() {
    val list = listOf(randomString)
    val newColourHex = "#ffffff"
    val modifiedList = listOf(randomString, newColourHex)
    `when`(minimalImagesUseCase.addCustomColor(modifiedList)).thenReturn(Completable.complete())

    minimalPresenter.handleColorPickerPositiveClick(newColourHex, list)

    verify(minimalImagesUseCase).addCustomColor(modifiedList)
    verify(minimalView).addColorToList(newColourHex)
    verify(minimalView).getScope()
    verify(minimalView).insertItemAndScrollToItemView(modifiedList.size)
    verify(minimalView).showAddColorSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should show error on handleColorPickerPositiveClick call failure`() {
    val list = listOf(randomString)
    val newColourHex = "#ffffff"
    val modifiedList = listOf(randomString, newColourHex)
    `when`(minimalImagesUseCase.addCustomColor(modifiedList)).thenReturn(
        Completable.error(Exception()))

    minimalPresenter.handleColorPickerPositiveClick(newColourHex, list)

    verify(minimalImagesUseCase).addCustomColor(modifiedList)
    verify(minimalView).getScope()
    verify(minimalView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show error on handleColorPickerPositiveClick call failure as color is already present`() {
    val list = listOf(randomString)

    minimalPresenter.handleColorPickerPositiveClick(randomString, list)

    verify(minimalView).showColorAlreadyPresentErrorMessageAndScrollToPosition(
        list.indexOf(randomString) + INITIAL_OFFSET)
  }

  @Test fun `should restore colors on handleUndoDeletionOptionClick call success`() {
    val restoreColorsModel = RestoreColorsModelFactory.getRestoreColorsModel()
    val restoreColorsPresenterEntity =
        RestoreColorsPresenterEntityMapper().mapToPresenterEntity(restoreColorsModel)
    val inOrder = inOrder(minimalView)
    `when`(minimalImagesUseCase.restoreColors()).thenReturn(Single.just(restoreColorsModel))

    minimalPresenter.handleUndoDeletionOptionClick()

    verify(minimalImagesUseCase).restoreColors()
    verify(minimalView).getScope()
    verify(minimalView).setColorList(restoreColorsPresenterEntity.colorsList)
    restoreColorsPresenterEntity.selectedItemsMap.keys.forEach {
      inOrder.verify(minimalView).addItemView(it + INITIAL_OFFSET)
    }
    verify(minimalView).showRestoreColorsSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should show error on handleUndoDeletionOptionClick call failure`() {
    `when`(minimalImagesUseCase.restoreColors()).thenReturn(Single.error(Exception()))

    minimalPresenter.handleUndoDeletionOptionClick()

    verify(minimalImagesUseCase).restoreColors()
    verify(minimalView).getScope()
    verify(minimalView).showUnableToRestoreColorsMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should show color picker dialog on handleClick call success`() {
    val position = 0
    val list = listOf<String>()
    val map = hashMapOf<Int, String>()

    minimalPresenter.handleClick(position, list, map)

    verify(minimalView).showColorPickerDialogAndAttachColorPickerListener()
  }

  @Test fun `should show exit selection mode message on handleClick call success`() {
    val position = 0
    val list = listOf<String>()
    val map = hashMapOf<Int, String>()
    map[1] = randomString

    minimalPresenter.handleClick(position, list, map)

    verify(minimalView).showExitSelectionModeToAddColorMessage()
  }

  @Test fun `should add to selected items map and show bottom panel on handleClick call success`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[1] = randomString
    minimalPresenter.isBottomPanelEnabled = false
    `when`(minimalView.addToSelectedItemsMap(0, randomString)).then {
      map[0] = randomString
      true
    }

    minimalPresenter.handleClick(position, list, map)

    assertTrue(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).addToSelectedItemsMap(0, randomString)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
    verify(minimalView).showBottomPanelWithAnimation()
  }

  @Test
  fun `should add to selected items map without show bottom panel on handleClick call success`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[1] = randomString
    minimalPresenter.isBottomPanelEnabled = true
    `when`(minimalView.addToSelectedItemsMap(0, randomString)).then {
      map[0] = randomString
      true
    }

    minimalPresenter.handleClick(position, list, map)

    assertTrue(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).addToSelectedItemsMap(0, randomString)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
  }

  @Test
  fun `should remove from selected items map and hide bottom panel on handleClick call success`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[0] = randomString
    minimalPresenter.isBottomPanelEnabled = true

    minimalPresenter.handleClick(position, list, map)

    assertFalse(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).removeFromSelectedItemsMap(position - INITIAL_OFFSET)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
    verify(minimalView).hideBottomLayoutWithAnimation()
  }

  @Test
  fun `should remove from selected items map without hiding bottom panel on handleClick call success`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[0] = randomString
    map[1] = randomString
    minimalPresenter.isBottomPanelEnabled = true

    minimalPresenter.handleClick(position, list, map)

    assertTrue(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).removeFromSelectedItemsMap(position - INITIAL_OFFSET)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
  }

  @Test fun `should show exit selection mode message on handleImageLongClick call success`() {
    val position = 0
    val list = listOf<String>()
    val map = hashMapOf<Int, String>()
    map[1] = randomString

    minimalPresenter.handleImageLongClick(position, list, map)

    verify(minimalView).showExitSelectionModeToAddColorMessage()
  }

  @Test
  fun `should add to selected items map and show bottom panel on handleImageLongClick call success`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[1] = randomString
    minimalPresenter.isBottomPanelEnabled = false
    `when`(minimalView.addToSelectedItemsMap(0, randomString)).then {
      map[0] = randomString
      true
    }

    minimalPresenter.handleImageLongClick(position, list, map)

    assertTrue(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).startSelection(position)
    verify(minimalView).addToSelectedItemsMap(0, randomString)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
    verify(minimalView).showBottomPanelWithAnimation()
  }

  @Test
  fun `should add to selected items map without show bottom panel on handleImageLongClick call success`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[1] = randomString
    minimalPresenter.isBottomPanelEnabled = true
    `when`(minimalView.addToSelectedItemsMap(0, randomString)).then {
      map[0] = randomString
      true
    }

    minimalPresenter.handleImageLongClick(position, list, map)

    assertTrue(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).startSelection(position)
    verify(minimalView).addToSelectedItemsMap(0, randomString)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
  }

  @Test
  fun `should remove from selected items map and hide bottom panel on handleImageLongClick call success`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[0] = randomString
    minimalPresenter.isBottomPanelEnabled = true

    minimalPresenter.handleImageLongClick(position, list, map)

    assertFalse(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).startSelection(position)
    verify(minimalView).removeFromSelectedItemsMap(position - INITIAL_OFFSET)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
    verify(minimalView).hideBottomLayoutWithAnimation()
  }

  @Test
  fun `should remove from selected items map without hiding bottom panel on handleImageLongClick call success`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[0] = randomString
    map[1] = randomString
    minimalPresenter.isBottomPanelEnabled = true

    minimalPresenter.handleImageLongClick(position, list, map)

    assertTrue(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).startSelection(position)
    verify(minimalView).removeFromSelectedItemsMap(position - INITIAL_OFFSET)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
  }

  @Test fun `should return false on isItemSelectable call success when index is zero`() {
    assertFalse(minimalPresenter.isItemSelectable(0))
  }

  @Test fun `should return true on isItemSelectable call success when index is not zero`() {
    assertTrue(minimalPresenter.isItemSelectable(1))
  }

  @Test fun `should return false on isItemSelected call when index is not present in map`() {
    val position = 1
    val map = hashMapOf<Int, String>()

    assertFalse(minimalPresenter.isItemSelected(position, map))
  }

  @Test fun `should return true on isItemSelected call when index is present in map`() {
    val position = 1
    val map = hashMapOf<Int, String>()
    map[position - INITIAL_OFFSET] = randomString

    assertTrue(minimalPresenter.isItemSelected(position, map))
  }

  @Test
  fun `should add to selected items map and show bottom panel on setItemSelected call success with selected as true`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[1] = randomString
    minimalPresenter.isBottomPanelEnabled = false
    `when`(minimalView.addToSelectedItemsMap(0, randomString)).then {
      map[0] = randomString
      true
    }

    minimalPresenter.setItemSelected(position, true, list, map)

    assertTrue(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).addToSelectedItemsMap(0, randomString)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
    verify(minimalView).showBottomPanelWithAnimation()
  }

  @Test
  fun `should add to selected items map without show bottom panel on setItemSelected call success with selected as true`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[1] = randomString
    minimalPresenter.isBottomPanelEnabled = true
    `when`(minimalView.addToSelectedItemsMap(0, randomString)).then {
      map[0] = randomString
      true
    }

    minimalPresenter.setItemSelected(position, true, list, map)

    assertTrue(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).addToSelectedItemsMap(0, randomString)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
  }

  @Test
  fun `should remove from selected items map and hide bottom panel on setItemSelected call success with selected as false`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[0] = randomString
    minimalPresenter.isBottomPanelEnabled = true

    minimalPresenter.setItemSelected(position, false, list, map)

    assertFalse(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).removeFromSelectedItemsMap(position - INITIAL_OFFSET)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
    verify(minimalView).hideBottomLayoutWithAnimation()
  }

  @Test
  fun `should remove from selected items map without hiding bottom panel on setItemSelected call success with selected as false`() {
    val position = 1
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[0] = randomString
    map[1] = randomString
    minimalPresenter.isBottomPanelEnabled = true

    minimalPresenter.setItemSelected(position, false, list, map)

    assertTrue(minimalPresenter.isBottomPanelEnabled)
    verify(minimalView).removeFromSelectedItemsMap(position - INITIAL_OFFSET)
    verify(minimalView).updateItemView(position)
    verify(minimalView).showAppBar()
    verify(minimalView).showCab(map.size)
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(postExecutionThread, minimalImagesUseCase, minimalView)
    minimalPresenter.detachView()
  }

  private fun verifyPostExecutionThreadSchedulerCall() {
    verify(postExecutionThread).scheduler
    verifyNoMoreInteractions(postExecutionThread)
  }
}