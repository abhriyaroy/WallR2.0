package zebrostudio.wallr100.presentation.minimal

import zebrostudio.wallr100.android.BasePresenter

interface MinimalContract {

  interface MinimalView

  interface MinimalPresenter : BasePresenter<MinimalView>

}