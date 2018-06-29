package zebrostudio.wallr100.ui.main

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import com.yalantis.guillotine.animation.GuillotineAnimation
import com.yalantis.guillotine.interfaces.GuillotineListener
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.fragmentContainer
import kotlinx.android.synthetic.main.activity_main.rootFrameLayout
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.guillotine_menu_layout.rootLinearLayoutGuillotineMenu
import kotlinx.android.synthetic.main.guillotine_menu_layout.view.hamburgerGuillotineMenu
import kotlinx.android.synthetic.main.item_guillotine_menu.view.imageviewGuillotineMenuItem
import kotlinx.android.synthetic.main.item_guillotine_menu.view.textviewGuillotineMenuItem
import kotlinx.android.synthetic.main.toolbar_layout.contentHamburger
import zebrostudio.wallr100.R
import zebrostudio.wallr100.ui.basefragment.BaseFragment
import zebrostudio.wallr100.ui.collection.CollectionFragment
import zebrostudio.wallr100.ui.wallpaper.WallpaperFragment
import zebrostudio.wallr100.ui.minimal.MinimalFragment
import zebrostudio.wallr100.utils.colorRes
import zebrostudio.wallr100.utils.drawableRes
import zebrostudio.wallr100.utils.infoToast
import zebrostudio.wallr100.utils.setOnDebouncedClickListener
import zebrostudio.wallr100.utils.stringRes
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContract.MainView, HasSupportFragmentInjector {

  @Inject
  internal lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
  @Inject
  internal lateinit var presenter: MainContract.MainPresenter

  private lateinit var guillotineMenuAnimation: GuillotineAnimation

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    initializeViews()
    addFragment(fragmentContainer.id, WallpaperFragment.newInstance(),
        WallpaperFragment.EXPLORE_FRAGMENT_TAG)
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
  }

  override fun onBackPressed() {
    presenter.handleBackPress()
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  override fun supportFragmentInjector() = fragmentDispatchingAndroidInjector

  override fun exitApp() {
    this.finish()
  }

  override fun showExitConfirmation() {
    infoToast(stringRes(R.string.exit_confirmation_message))
  }

  override fun closeNavigationMenu() {
    guillotineMenuAnimation.close()
  }

  override fun showPreviousFragment() {
    supportFragmentManager.popBackStack()
  }

  override fun getFragmentAtStackTop(): String {
    return supportFragmentManager
        .getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
  }

  private inline fun <reified T : BaseFragment> addFragment(
    @IdRes id: Int,
    fragment: T,
    fragmentTag: String
  ) {
    if (!fragmentExistsOnStackTop(fragmentTag)) {
      if (fragmentTag == WallpaperFragment.EXPLORE_FRAGMENT_TAG) {
        clearStack()
      }

      supportFragmentManager
          .beginTransaction()
          .replace(id, fragment, fragmentTag)
          .addToBackStack(fragmentTag)
          .commitAllowingStateLoss()

      fragment.fragmentTag = fragmentTag
    }
  }

  private fun fragmentExistsOnStackTop(fragmentTag: String): Boolean {
    if (supportFragmentManager.backStackEntryCount == 0)
      return false
    return getFragmentAtStackTop() == fragmentTag
  }

  private fun initializeViews() {
    val guillotineMenu = LayoutInflater.from(this)
        .inflate(R.layout.guillotine_menu_layout, null)
    rootFrameLayout.addView(guillotineMenu)

    setSupportActionBar(toolbar)

    val guillotineListener = object : GuillotineListener {
      override fun onGuillotineOpened() {
        presenter.notifyNavigationMenuOpened()
      }

      override fun onGuillotineClosed() {
        presenter.notifyNavigationMenuClosed()
      }
    }

    val rippleDuration = 250
    guillotineMenuAnimation = GuillotineAnimation.GuillotineBuilder(
        guillotineMenu,
        guillotineMenu.hamburgerGuillotineMenu,
        contentHamburger)
        .setStartDelay(rippleDuration.toLong())
        .setActionBarViewForAnimation(toolbar)
        .setGuillotineListener(guillotineListener)
        .setClosedOnStart(true)
        .build()

    setUpGuillotineMenuItems(buildGuillotineMenuItems())
  }

  private fun buildGuillotineMenuItems(): List<Triple<Int, Int, MenuItem>> {
    // Declare mutable list containing names and icon resources of guillotine menu items
    val menuItemDetails = mutableListOf<Triple<Int, Int, MenuItem>>()
    menuItemDetails.add(Triple(R.string.guillotine_explore_title, R.drawable.ic_explore_white,
        MenuItem.EXPLORE))
    menuItemDetails.add(Triple(R.string.guillotine_toppicks_title, R.drawable.ic_toppicks_white,
        MenuItem.TOP_PICKS))
    menuItemDetails.add(Triple(R.string.guillotine_categories_title, R.drawable.ic_categories_white,
        MenuItem.CATEGORIES))
    menuItemDetails.add(Triple(R.string.guillotine_minimal_title, R.drawable.ic_minimal_white,
        MenuItem.MINIMAL))
    menuItemDetails.add(
        Triple(R.string.guillotine_collection_title, R.drawable.ic_collections_white,
            MenuItem.COLLECTION))
    menuItemDetails.add(Triple(R.string.guillotine_feedback_title, R.drawable.ic_feedback_white,
        MenuItem.FEEDBACK))
    menuItemDetails.add(Triple(R.string.guillotine_buypro_title, R.drawable.ic_buypro_black,
        MenuItem.BUY_PRO))
    return menuItemDetails
  }

  private fun setUpGuillotineMenuItems(guillotineMenuItems: List<Triple<Int, Int, MenuItem>>) {
    // Programmatically add guillotine menu items
    val layoutInflater = LayoutInflater.from(this)
    val itemIterator = guillotineMenuItems.iterator()
    itemIterator.forEach {
      val guillotineMenuItemView = layoutInflater
          .inflate(R.layout.item_guillotine_menu, null)
      rootLinearLayoutGuillotineMenu?.addView(guillotineMenuItemView)
      guillotineMenuItemView.id = it.first
      guillotineMenuItemView.textviewGuillotineMenuItem.text =
          stringRes(it.first)
      guillotineMenuItemView.imageviewGuillotineMenuItem.setImageDrawable(
          drawableRes(it.second))
      // Make the background white and text color black for the buy pro guillotine menu item
      if (!itemIterator.hasNext()) {
        guillotineMenuItemView.setBackgroundColor(colorRes(R.color.color_white))
        guillotineMenuItemView.textviewGuillotineMenuItem
            .setTextColor(colorRes(R.color.color_black))
      }
      val menuItem = it.third
      guillotineMenuItemView.setOnDebouncedClickListener {
        clickListener(menuItem)
      }
    }
  }

  private fun clickListener(item: MenuItem) {
    when (item) {
      MenuItem.EXPLORE -> addFragment(fragmentContainer.id,
          WallpaperFragment.newInstance(), WallpaperFragment.EXPLORE_FRAGMENT_TAG)
      MenuItem.TOP_PICKS -> addFragment(fragmentContainer.id, WallpaperFragment.newInstance(),
          WallpaperFragment.TOP_PICKS_FRAGMENT_TAG)
      MenuItem.CATEGORIES -> addFragment(fragmentContainer.id, WallpaperFragment.newInstance(),
          WallpaperFragment.CATEGORIES_FRAGMENT_TAG)
      MenuItem.MINIMAL -> addFragment(fragmentContainer.id,
          MinimalFragment.newInstance(), MinimalFragment.MINIMAL_FRAGMENT_TAG)
      MenuItem.COLLECTION -> addFragment(fragmentContainer.id,
          CollectionFragment.newInstance(), CollectionFragment.COLLECTION_FRAGMENT_TAG)
      MenuItem.FEEDBACK -> {
        // TODO : Add feedback implementation
      }
      MenuItem.BUY_PRO -> {
        // TODO : Add buy pro section
      }
    }
    closeNavigationMenu()
  }

  private fun clearStack() {
    var backStackEntry = supportFragmentManager.backStackEntryCount
    if (backStackEntry > 0) {
      while (backStackEntry > 0) {
        supportFragmentManager.popBackStack()
        backStackEntry -= 1
      }
    }
  }

  private enum class MenuItem {
    EXPLORE,
    TOP_PICKS,
    CATEGORIES,
    MINIMAL,
    COLLECTION,
    FEEDBACK,
    BUY_PRO
  }

}