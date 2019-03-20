package zebrostudio.wallr100.presentation.minimal

import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewPresenter

interface MinimalContract {

  interface MinimalView : BaseView {
    fun updateAllItems()
    fun showUnableToGetColorsErrorMessage()
    fun showGenericErrorMessage()
    fun updateItemView(index: Int)
    fun removeItemView(index: Int)
    fun addAndScrollToItemView(index: Int)
    fun showCab(size: Int)
    fun hideCab()
    fun showBottomPanelWithAnimation()
    fun hideBottomLayoutWithAnimation()
    fun startSelection(position: Int)
    fun showDeselectBeforeDeletionMessage(numberOfItemsToBeDeselected: Int)
    fun showDeleteColorsErrorMessage()
    fun clearCabIfActive()
    fun showColorPickerDialog()
  }

  interface MinimalPresenter : BasePresenter<MinimalView> {
    fun attachMinimalImageRecyclerViewPresenter(presenter: MinimalRecyclerViewPresenter)
    fun detachMinimalImageRecyclerViewPresenter()
    fun handleViewCreated()
    fun updateSelectionChange(index: Int, size: Int)
    fun handleItemLongClick(position: Int)
    fun handleScroll(yAxisMovement: Int)
    fun handleDeleteMenuItemClick()
    fun handleCabDestroyed()
    fun handleSpinnerOptionChanged(position: Int)
    fun handleColorPickerPositiveClick(text: String)
  }

}