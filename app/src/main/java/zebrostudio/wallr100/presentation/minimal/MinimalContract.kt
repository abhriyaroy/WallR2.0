package zebrostudio.wallr100.presentation.minimal

import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView

interface MinimalContract {

  interface MinimalView : BaseView {
    fun showColors(colorsList: List<String>)
    fun showUnableToGetColorsErrorMessage()
    fun showGenericErrorMessage()
  }

  interface MinimalPresenter : BasePresenter<MinimalView> {
    fun handleViewCreated()
  }

}