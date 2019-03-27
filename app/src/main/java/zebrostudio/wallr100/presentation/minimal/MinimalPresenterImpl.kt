package zebrostudio.wallr100.presentation.minimal

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.data.exception.UnableToGetMinimalColorsException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.MinimalImagesUseCase
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalView
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.GRADIENT
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.MATERIAL
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.PLASMA
import zebrostudio.wallr100.presentation.minimal.mapper.RestoreColorsPresenterEntityMapper
import java.util.Collections
import java.util.Random
import java.util.TreeMap

const val MINIMUM_SCROLL_DIST = 15
const val INITIAL_SIZE = 0
const val INITIAL_OFFSET = 1
const val MINIMUM_COLOR_LIST_SIZE = 20
const val FIRST_ELEMENT_INDEX = 1
const val INCREMENT_BY_1 = 1
const val INCREMENT_BY_2 = 2

class MinimalPresenterImpl(
  private val minimalImagesUseCase: MinimalImagesUseCase,
  private val postExecutionThread: PostExecutionThread
) : MinimalPresenter {

  internal var isBottomPanelEnabled = false
  internal var selectionSize = INITIAL_SIZE
  internal var minimalView: MinimalView? = null
  internal var multiColorImageType: MultiColorImageType? = MATERIAL
  internal var shouldUpdateAllItems = true

  override fun attachView(view: MinimalView) {
    minimalView = view
  }

  override fun detachView() {
    minimalView = null
  }

  override fun handleViewCreated() {
    if (minimalImagesUseCase.isCustomColorListPresent()) {
      minimalImagesUseCase.getCustomColors()
    } else {
      minimalImagesUseCase.getDefaultColors()
    }.observeOn(postExecutionThread.scheduler)
        .autoDisposable(minimalView!!.getScope())
        .subscribe({
          minimalView?.setColorList(it)
          minimalView?.updateAllItems()
        }, {
          if (it is UnableToGetMinimalColorsException) {
            minimalView?.showUnableToGetColorsErrorMessage()
          } else {
            minimalView?.showGenericErrorMessage()
          }
        })
  }

  override fun handleOnScrolled(yAxisMovement: Int) {
    if (isBottomPanelEnabled && yAxisMovement > MINIMUM_SCROLL_DIST) {
      hideBottomPanelWithAnimationInView()
    } else if (!isBottomPanelEnabled && yAxisMovement < -MINIMUM_SCROLL_DIST && selectionSize > 1) {
      showBottomPanelWithAnimationInView()
    }
  }

  override fun handleDeleteMenuItemClick(
    colorList: List<String>,
    selectedItemsMap: HashMap<Int, String>
  ) {
    numberOfItemsToBeDeselectedToStartDeletion(colorList, selectedItemsMap).let {
      if (it == INITIAL_SIZE) {
        val reversedSelectedItems = TreeMap<Int, String>(Collections.reverseOrder())
        minimalImagesUseCase.modifyColors(colorList, selectedItemsMap)
            .doOnSuccess {
              reversedSelectedItems.putAll(selectedItemsMap)
              minimalView?.clearSelectedItemsMap()
              minimalView?.setColorList(it)
            }
            .observeOn(postExecutionThread.scheduler)
            .autoDisposable(minimalView!!.getScope())
            .subscribe({
              reversedSelectedItems.keys.forEach {
                minimalView?.removeItemView(it + INITIAL_OFFSET)
              }
              minimalView?.showUndoDeletionOption(reversedSelectedItems.size)
              shouldUpdateAllItems = false
              minimalView?.clearCabIfActive()
            }, {
              shouldUpdateAllItems = true
              minimalView?.clearCabIfActive()
              minimalView?.showDeleteColorsErrorMessage()
            })
      } else {
        minimalView?.showDeselectBeforeDeletionMessage(it)
      }
    }
  }

  override fun handleCabDestroyed() {
    minimalView?.clearSelectedItemsMap()
    if (isBottomPanelEnabled) {
      hideBottomPanelWithAnimationInView()
      selectionSize = INITIAL_SIZE
    }
    if (shouldUpdateAllItems) {
      minimalView?.updateAllItems()
    }
    shouldUpdateAllItems = true
  }

  override fun handleSpinnerOptionChanged(position: Int) {
    when (position) {
      MATERIAL.ordinal -> multiColorImageType = MATERIAL
      GRADIENT.ordinal -> multiColorImageType = GRADIENT
      PLASMA.ordinal -> multiColorImageType = PLASMA
    }
  }

  override fun handleColorPickerPositiveClick(hexValue: String, colorList: List<String>) {
    if (!colorList.contains(hexValue)) {
      colorList.toMutableList().apply {
        add(hexValue)
        minimalImagesUseCase.addCustomColor(this.toList())
            .doOnComplete {
              minimalView?.addColorToList(hexValue)
            }.observeOn(postExecutionThread.scheduler)
            .autoDisposable(minimalView!!.getScope())
            .subscribe({
              minimalView?.insertItemAndScrollToItemView(size)
              minimalView?.showAddColorSuccessMessage()
            }, {
              minimalView?.showGenericErrorMessage()
            })
      }
    } else {
      minimalView?.showColorAlreadyPresentErrorMessageAndScrollToPosition(
          colorList.indexOf(hexValue) + INITIAL_OFFSET)
    }
  }

  override fun handleUndoDeletionOptionClick() {
    minimalImagesUseCase.restoreColors()
        .map {
          RestoreColorsPresenterEntityMapper().mapToPresenterEntity(it)
        }.observeOn(postExecutionThread.scheduler)
        .autoDisposable(minimalView!!.getScope())
        .subscribe({
          minimalView?.setColorList(it.colorsList)
          it.selectedItemsMap.keys.forEach {
            minimalView?.addItemView(it + INITIAL_OFFSET)
          }
          minimalView?.showRestoreColorsSuccessMessage()
        }, {
          minimalView?.showUnableToRestoreColorsMessage()
        })
  }

  override fun handleMultiSelectMenuClick() {
    minimalView!!.getTopAndBottomVisiblePositions().let { pair ->
      (Random().nextInt(pair.second - pair.first + INITIAL_OFFSET) + pair.first).let {
        minimalView?.selectItem(it)
        minimalView?.selectItem(it + INCREMENT_BY_1)
        minimalView?.selectItem(it + INCREMENT_BY_2)
      }
    }
  }

  override fun handleClick(
    position: Int,
    colorList: List<String>,
    selectedItemsMap: HashMap<Int, String>
  ) {
    if (selectedItemsMap.size == INITIAL_SIZE) {
      if (position == INITIAL_SIZE) {
        minimalView?.showColorPickerDialogAndAttachColorPickerListener()
      } else {
        IllegalStateException("Feature not implemented yet")
      }
    } else {
      if (position != INITIAL_SIZE) {
        toggleSelected(position, colorList, selectedItemsMap)
      } else {
        minimalView?.showExitSelectionModeToAddColorMessage()
      }
    }
  }

  override fun handleImageLongClick(
    position: Int, colorList: List<String>, selectedItemsMap: HashMap<Int, String>
  ): Boolean {
    if (position != INITIAL_SIZE) {
      toggleSelected(position, colorList, selectedItemsMap)
      minimalView?.startSelection(position)
    }
    return false
  }

  override fun isItemSelectable(index: Int): Boolean {
    return index != INITIAL_SIZE
  }

  override fun isItemSelected(index: Int, selectedItemsMap: HashMap<Int, String>): Boolean {
    return selectedItemsMap.containsKey(index - INITIAL_OFFSET)
  }

  override fun setItemSelected(
    index: Int,
    selected: Boolean,
    colorList: List<String>,
    selectedItemsMap: HashMap<Int, String>
  ) {
    if (index != INITIAL_SIZE) {
      (index - INITIAL_OFFSET).let {
        if (selected) {
          selectedItemsMap[it] = colorList[it]
        } else {
          selectedItemsMap.remove(it)
        }
      }
      updateSelectionChange(index, selectedItemsMap.size)
    }
  }

  private fun numberOfItemsToBeDeselectedToStartDeletion(
    colorList: List<String>,
    selectedItemsMap: HashMap<Int, String>
  ): Int {
    return if (colorList.size - selectedItemsMap.size >= MINIMUM_COLOR_LIST_SIZE) {
      INITIAL_SIZE
    } else {
      MINIMUM_COLOR_LIST_SIZE - (colorList.size - selectedItemsMap.size)
    }
  }

  private fun toggleSelected(
    index: Int,
    colorList: List<String>,
    selectedItemsMap: HashMap<Int, String>
  ) {
    (index - INITIAL_OFFSET).let {
      if (!selectedItemsMap.containsKey(it)) {
        minimalView?.addToSelectedItemsMap(it, colorList[it])
      } else {
        minimalView?.removeFromSelectedItemsMap(it)
      }
    }
    updateSelectionChange(index, selectedItemsMap.size)
  }

  private fun updateSelectionChange(index: Int, size: Int) {
    selectionSize = size
    minimalView?.updateItemView(index)
    minimalView?.showAppBar()
    minimalView?.showCab(selectionSize)
    if (selectionSize == FIRST_ELEMENT_INDEX && isBottomPanelEnabled) {
      hideBottomPanelWithAnimationInView()
    } else if (selectionSize > FIRST_ELEMENT_INDEX && !isBottomPanelEnabled) {
      showBottomPanelWithAnimationInView()
    } else if (selectionSize == INITIAL_SIZE) {
      hideBottomPanelWithAnimationInView()
      minimalView?.hideCab()
    }
  }

  private fun showBottomPanelWithAnimationInView() {
    minimalView?.showBottomPanelWithAnimation()
    isBottomPanelEnabled = true
  }

  private fun hideBottomPanelWithAnimationInView() {
    minimalView?.hideBottomLayoutWithAnimation()
    isBottomPanelEnabled = false
  }
}

enum class MultiColorImageType {
  MATERIAL,
  GRADIENT,
  PLASMA
}