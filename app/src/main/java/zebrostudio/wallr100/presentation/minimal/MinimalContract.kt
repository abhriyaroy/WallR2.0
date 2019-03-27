package zebrostudio.wallr100.presentation.minimal

import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView

interface MinimalContract {

  interface MinimalView : BaseView {
    fun showAppBar()
    fun updateAllItems()
    fun showUnableToGetColorsErrorMessage()
    fun showGenericErrorMessage()
    fun updateItemView(index: Int)
    fun removeItemView(index: Int)
    fun addItemView(index: Int)
    fun insertItemAndScrollToItemView(index: Int)
    fun showAddColorSuccessMessage()
    fun showCab(size: Int)
    fun hideCab()
    fun showBottomPanelWithAnimation()
    fun hideBottomLayoutWithAnimation()
    fun startSelection(position: Int)
    fun showDeselectBeforeDeletionMessage(numberOfItemsToBeDeselected: Int)
    fun showDeleteColorsErrorMessage()
    fun clearCabIfActive()
    fun showColorPickerDialogAndAttachColorPickerListener()
    fun showColorAlreadyPresentErrorMessageAndScrollToPosition(position: Int)
    fun showExitSelectionModeToAddColorMessage()
    fun showUndoDeletionOption(size: Int)
    fun showUnableToRestoreColorsMessage()
    fun showRestoreColorsSuccessMessage()
    fun getTopAndBottomVisiblePositions(): Pair<Int, Int>

    fun addToSelectedItemsMap(position: Int, hexValue: String)
    fun removeFromSelectedItemsMap(item: Int)
    fun clearSelectedItemsMap()
    fun setColorList(list: List<String>)
    fun addColorToList(hexValue: String)
    fun selectItem(position: Int)
  }

  interface MinimalPresenter : BasePresenter<MinimalView> {
    fun handleViewCreated()
    fun handleOnScrolled(yAxisMovement: Int)
    fun handleDeleteMenuItemClick(colorList: List<String>, selectedItemsMap: HashMap<Int, String>)
    fun handleCabDestroyed()
    fun handleSpinnerOptionChanged(position: Int)
    fun handleColorPickerPositiveClick(hexValue: String, colorList: List<String>)
    fun handleUndoDeletionOptionClick()
    fun handleMultiSelectMenuClick()

    fun handleClick(position: Int, colorList: List<String>, selectedItemsMap: HashMap<Int, String>)
    fun handleImageLongClick(
      position: Int,
      colorList: List<String>,
      selectedItemsMap: HashMap<Int, String>
    ): Boolean

    fun isItemSelectable(index: Int): Boolean
    fun isItemSelected(index: Int, selectedItemsMap: HashMap<Int, String>): Boolean
    fun setItemSelected(
      index: Int,
      selected: Boolean,
      colorList: List<String>,
      selectedItemsMap: HashMap<Int, String>
    )
  }

}