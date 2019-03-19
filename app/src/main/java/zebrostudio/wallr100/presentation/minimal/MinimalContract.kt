package zebrostudio.wallr100.presentation.minimal

import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewPresenter

interface MinimalContract {

  interface MinimalView : BaseView {
    fun updateUi()
    fun showUnableToGetColorsErrorMessage()
    fun showGenericErrorMessage()
    fun updateViewItem(index: Int)
    fun showCab(size: Int)
    fun hideCab()
    fun showBottomPanelWithAnimation()
    fun hideBottomLayoutWithAnimation()
    fun startSelection(position: Int)
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
  }

}