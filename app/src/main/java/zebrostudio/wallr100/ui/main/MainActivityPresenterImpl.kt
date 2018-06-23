package zebrostudio.wallr100.ui.main

import android.os.Handler
import zebrostudio.wallr100.ui.categories.CategoriesFragment
import zebrostudio.wallr100.ui.collection.CollectionFragment
import zebrostudio.wallr100.ui.explore.ExploreFragment
import zebrostudio.wallr100.ui.minimal.MinimalFragment
import zebrostudio.wallr100.ui.toppicks.ToppicksFragment

class MainActivityPresenterImpl
constructor(private var dataRepository: DataRepository) : MainContract.MainPresenter {

  private var backPressedOnce: Boolean = false
  private var mainView: MainContract.MainView? = null
  private var isGuillotineMenuOpen = false
  override fun attachView(view: MainContract.MainView) {
    mainView = view
  }

  override fun detachView() {
    mainView = null
  }

  override fun handleBackPress() {
    if (isGuillotineMenuOpen) {
      mainView?.closeNavigationMenu()
    } else {
      if (dataRepository.retrieveCurrentFragmentName() == ExploreFragment.EXPLORE_FRAGMENT_TAG) {
        if (backPressedOnce) {
          mainView?.exitApp()
        } else {
          backPressedOnce = true
          mainView?.showExitConfirmation()
          Handler().postDelayed({ backPressedOnce = false }, 2000)
        }
      } else {
        mainView?.showPreviousFragment()
      }
    }
  }

  override fun notifyNavigationMenuOpened() {
    isGuillotineMenuOpen = true
  }

  override fun notifyNavigationMenuClosed() {
    isGuillotineMenuOpen = false
  }

  override fun exploreMenuItemClicked() {
    if (dataRepository.retrieveCurrentFragmentName() != ExploreFragment.EXPLORE_FRAGMENT_TAG){
      mainView?.showExploreFragment()
      mainView?.closeNavigationMenu()
    }
  }

  override fun toppicksMenuItemClicked() {
    if (dataRepository.retrieveCurrentFragmentName() != ToppicksFragment.TOPPICKS_FRAGMENT_TAG){
      mainView?.showTopPicksFragment()
      mainView?.closeNavigationMenu()
    }
  }

  override fun categoriesMenuItemClicked() {
    if (dataRepository.retrieveCurrentFragmentName() != CategoriesFragment.CATEGORIES_FRAGMENT_TAG){
      mainView?.showCategoriesFragment()
      mainView?.closeNavigationMenu()
    }
  }

  override fun minimalMenuItemClicked() {
    if (dataRepository.retrieveCurrentFragmentName() != MinimalFragment.MINIMAL_FRAGMENT_TAG){
      mainView?.showMinimalFragment()
      mainView?.closeNavigationMenu()
    }
  }

  override fun collectionMenuItemClicked() {
    if (dataRepository.retrieveCurrentFragmentName() != CollectionFragment.COLLECTION_FRAGMENT_TAG){
      mainView?.showCollectionFragment()
      mainView?.closeNavigationMenu()
    }
  }

}
