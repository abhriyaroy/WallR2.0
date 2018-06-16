package zebrostudio.wallr100.ui.main

import zebrostudio.wallr100.BasePresenter
import zebrostudio.wallr100.BaseView

interface MainContract {

  interface MainView : BaseView<MainPresenter> {
    fun showExitToast()

    fun exitApp()

    fun showPreviousFragment()

    fun closeGuillotineMenu()
  }

  interface MainPresenter : BasePresenter {

    fun attach(view: MainView)

    fun detach()

    fun handleBackPress()

    fun notifyGuillotineMenuOpened()

    fun notifyGuillotineMenuClosed()

  }
}