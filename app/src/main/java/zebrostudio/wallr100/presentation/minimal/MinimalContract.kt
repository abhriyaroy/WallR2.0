package zebrostudio.wallr100.presentation.minimal

import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewPresenter

interface MinimalContract {

  interface MinimalView : BaseView {
    fun showColors()
    fun showUnableToGetColorsErrorMessage()
    fun showGenericErrorMessage()
  }

  interface MinimalPresenter : BasePresenter<MinimalView> {
    fun attachMinimalImageRecyclerViewPresenter(presenter: MinimalRecyclerViewPresenter)
    fun detachMinimalImageRecyclerViewPresenter()
    fun handleViewCreated()
  }

}