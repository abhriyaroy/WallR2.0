package zebrostudio.wallr100.ui.main

import zebrostudio.wallr100.BasePresenter

interface MainContract {

  interface MainView {

    fun showExitConfirmation()
    fun exitApp()
    fun closeNavigationMenu()
    fun showPreviousFragment()

    fun showExploreFragment()
    fun showTopPicksFragment()
    fun showCategoriesFragment()
    fun showMinimalFragment()
    fun showCollectionFragment()

  }

  interface MainPresenter : BasePresenter<MainView> {

    fun handleBackPress()
    fun notifyNavigationMenuOpened()
    fun notifyNavigationMenuClosed()

    fun exploreMenuItemClicked()
    fun toppicksMenuItemClicked()
    fun categoriesMenuItemClicked()
    fun minimalMenuItemClicked()
    fun collectionMenuItemClicked()

  }
}