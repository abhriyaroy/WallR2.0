package zebrostudio.wallr100.ui.main

import zebrostudio.wallr100.BasePresenter
import zebrostudio.wallr100.BaseView

interface MainContract {

  interface MainView : BaseView<MainPresenter>{

  }

  interface MainPresenter : BasePresenter{

    fun setView()

  }
}