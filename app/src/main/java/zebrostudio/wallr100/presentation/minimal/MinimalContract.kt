package zebrostudio.wallr100.presentation.minimal

import zebrostudio.wallr100.android.ui.adapters.MinimalViewHolder
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView

interface MinimalContract {

  interface MinimalView : BaseView {
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
    fun clearCabIfActive(renewView : Boolean)
    fun showColorPickerDialogAndAttachColorPickerListener()
    fun showColorAlreadyPresentErrorMessage(position: Int)
    fun showExitSelectionModeToAddColorMessage()
  }

  interface MinimalPresenter : BasePresenter<MinimalView> {
    fun handleViewCreated()
    fun updateSelectionChange(index: Int, size: Int)
    fun handleScroll(yAxisMovement: Int)
    fun handleDeleteMenuItemClick()
    fun handleCabDestroyed(updateEntireView: Boolean)
    fun handleSpinnerOptionChanged(position: Int)
    fun handleColorPickerPositiveClick(text: String)

    fun getItemCount(): Int
    fun onBindRepositoryRowViewAtPosition(holder: MinimalViewHolder, position: Int)
    fun handleClick(position: Int, itemView: ItemViewHolder)
    fun handleImageLongClick(position: Int, itemView: ItemViewHolder)
    fun isItemSelectable(index: Int): Boolean
    fun isItemSelected(index: Int): Boolean
    fun setItemSelected(index: Int, selected: Boolean)
    fun numberOfItemsToBeDeselectedToStartDeletion(): Int
  }

  interface ItemViewHolder {
    fun showAddImageLayout()
    fun hideAddImageLayout()
    fun setImageViewColor(colorHexCode: String)
    fun showSelectedIndicator()
    fun hideSelectedIndicator()
    fun attachClickListener()
    fun attachLongClickListener()
  }

}