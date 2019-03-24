package zebrostudio.wallr100.presentation.minimal

import com.uber.autodispose.autoDisposable
import zebrostudio.wallr100.data.exception.UnableToGetSolidColorsException
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.MinimalImagesUseCase
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalView
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.GRADIENT
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.MATERIAL
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.PLASMA
import java.util.Collections
import java.util.TreeMap

const val MINIMUM_SCROLL_DIST = 15
const val INITIAL_SIZE = 0
const val INITIAL_OFFSET = 1
const val MINIMUM_COLOR_LIST_SIZE = 20
const val FIRST_ELEMENT_INDEX = 1

class MinimalPresenterImpl(
  private val minimalImagesUseCase: MinimalImagesUseCase,
  private val postExecutionThread: PostExecutionThread
) : MinimalPresenter {

  private var isBottomPanelEnabled = false
  private var selectionSize = INITIAL_SIZE
  private var minimalView: MinimalView? = null
  private var forceSmoothScroll: Boolean = false
  private var multiColorImageType: MultiColorImageType? = MATERIAL
  private var shouldUpdateAllItems = true

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
          if (it is UnableToGetSolidColorsException) {
            minimalView?.showUnableToGetColorsErrorMessage()
          } else {
            minimalView?.showGenericErrorMessage()
          }
        })
  }

  override fun handleOnScrolled(yAxisMovement: Int) {
    if (isBottomPanelEnabled && yAxisMovement > MINIMUM_SCROLL_DIST && !forceSmoothScroll) {
      minimalView?.hideBottomLayoutWithAnimation()
      isBottomPanelEnabled = false
    } else if (!isBottomPanelEnabled && yAxisMovement < -MINIMUM_SCROLL_DIST && selectionSize > 1) {
      minimalView?.showBottomPanelWithAnimation()
      isBottomPanelEnabled = true
    }
  }

  override fun handleDeleteMenuItemClick(
    colorList: List<String>,
    selectedItemsMap: HashMap<Int, Boolean>
  ) {
    numberOfItemsToBeDeselectedToStartDeletion(colorList, selectedItemsMap).let {
      if (it == INITIAL_SIZE) {
        val reversedSelectedItems = TreeMap<Int, Boolean>(Collections.reverseOrder())
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
              shouldUpdateAllItems = false
              minimalView?.clearCabIfActive()
            }, {
              System.out.println(it.printStackTrace())
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
      minimalView?.hideBottomLayoutWithAnimation()
      isBottomPanelEnabled = false
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
      minimalImagesUseCase.addCustomColor(colorList)
          .doOnSubscribe {
            minimalView?.addColorToList(hexValue)
          }.observeOn(postExecutionThread.scheduler)
          .autoDisposable(minimalView!!.getScope())
          .subscribe({
            minimalView?.addColorAndScrollToItemView(colorList.size)
            minimalView?.showAddColorSuccessMessage()
          }, {
            minimalView?.showGenericErrorMessage()
          })
    } else {
      minimalView?.showColorAlreadyPresentErrorMessage(colorList.indexOf(hexValue) + INITIAL_OFFSET)
    }
  }

  override fun handleClick(
    position: Int,
    selectedItemsMap: HashMap<Int, Boolean>
  ) {
    if (selectedItemsMap.size == INITIAL_SIZE) {
      if (position == INITIAL_SIZE) {
        minimalView?.showColorPickerDialogAndAttachColorPickerListener()
      } else {

      }
    } else {
      if (position != INITIAL_SIZE) {
        toggleSelected(position, selectedItemsMap)
      } else {
        minimalView?.showExitSelectionModeToAddColorMessage()
      }
    }
  }

  override fun handleImageLongClick(
    position: Int, selectedItemsMap: HashMap<Int, Boolean>
  ) : Boolean {
    if (position != INITIAL_SIZE) {
      toggleSelected(position, selectedItemsMap)
      minimalView?.startSelection(position)
    }
    return false
  }

  override fun isItemSelectable(index: Int): Boolean {
    return index != INITIAL_SIZE
  }

  override fun isItemSelected(index: Int, selectedItemsMap: HashMap<Int, Boolean>): Boolean {
    return selectedItemsMap.containsKey(index - INITIAL_OFFSET)
  }

  override fun setItemSelected(
    index: Int,
    selected: Boolean,
    selectedItemsMap: HashMap<Int, Boolean>
  ) {
    if (index != INITIAL_SIZE) {
      if (selected) {
        selectedItemsMap[index - INITIAL_OFFSET] = true
      } else {
        selectedItemsMap.remove(index - INITIAL_OFFSET)
      }
      updateSelectionChange(index, selectedItemsMap.size)
    }
  }

  override fun numberOfItemsToBeDeselectedToStartDeletion(
    colorList: List<String>,
    selectedItemsMap: HashMap<Int, Boolean>
  ): Int {
    return if (colorList.size - selectedItemsMap.size >= MINIMUM_COLOR_LIST_SIZE) {
      INITIAL_SIZE
    } else {
      MINIMUM_COLOR_LIST_SIZE - (colorList.size - selectedItemsMap.size)
    }
  }

  private fun toggleSelected(
    index: Int,
    selectedItemsMap: HashMap<Int, Boolean>
  ) {
    (index - INITIAL_OFFSET).let {
      if (!selectedItemsMap.containsKey(it)) {
        minimalView?.addToSelectedItemsMap(it)
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
      minimalView?.hideBottomLayoutWithAnimation()
      isBottomPanelEnabled = false
    } else if (selectionSize > FIRST_ELEMENT_INDEX && !isBottomPanelEnabled) {
      isBottomPanelEnabled = true
      minimalView?.showBottomPanelWithAnimation()
    } else if (selectionSize == INITIAL_SIZE) {
      isBottomPanelEnabled = false
      minimalView?.hideBottomLayoutWithAnimation()
      minimalView?.hideCab()
    }
  }
}

enum class MultiColorImageType {
  MATERIAL,
  GRADIENT,
  PLASMA
}