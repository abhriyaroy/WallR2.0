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
    fun addColorAndScrollToItemView(index: Int)
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
    fun showColorAlreadyPresentErrorMessage(position: Int)
    fun showExitSelectionModeToAddColorMessage()

    fun addToSelectedItemsMap(position: Int)
    fun removeFromSelectedItemsMap(item: Int)
    fun clearSelectedItemsMap()
    fun setColorList(list: List<String>)
    fun addColorToList(hexValue: String)
  }

  interface MinimalPresenter : BasePresenter<MinimalView> {
    fun handleViewCreated()
    fun handleOnScrolled(yAxisMovement: Int)
    fun handleDeleteMenuItemClick(colorList: List<String>, selectedItemsMap: HashMap<Int, Boolean>)
    fun handleCabDestroyed()
    fun handleSpinnerOptionChanged(position: Int)
    fun handleColorPickerPositiveClick(hexValue: String, colorList: List<String>)

    fun handleClick(position: Int, selectedItemsMap: HashMap<Int, Boolean>)
    fun handleImageLongClick(position: Int, selectedItemsMap: HashMap<Int, Boolean>): Boolean
    fun isItemSelectable(index: Int): Boolean
    fun isItemSelected(index: Int, selectedItemsMap: HashMap<Int, Boolean>): Boolean
    fun setItemSelected(index: Int, selected: Boolean, selectedItemsMap: HashMap<Int, Boolean>)
    fun numberOfItemsToBeDeselectedToStartDeletion(
      colorList: List<String>,
      selectedItemsMap: HashMap<Int, Boolean>
    ): Int
  }

}