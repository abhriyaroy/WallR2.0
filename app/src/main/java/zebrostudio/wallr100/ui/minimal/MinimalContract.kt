package zebrostudio.wallr100.ui.minimal

import zebrostudio.wallr100.BasePresenter

interface MinimalContract {

  interface MinimalView

  interface MinimalPresenter : BasePresenter<MinimalView>

}