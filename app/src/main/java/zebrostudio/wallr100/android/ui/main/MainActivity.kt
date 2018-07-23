package zebrostudio.wallr100.android.ui.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
import zebrostudio.wallr100.android.ui.basefragment.BaseFragment
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.collection.CollectionFragment
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment
import zebrostudio.wallr100.android.ui.minimal.MinimalFragment
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.drawableRes
import zebrostudio.wallr100.android.utils.infoToast
import zebrostudio.wallr100.android.utils.setOnDebouncedClickListener
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.withDelayOnMain
import zebrostudio.wallr100.presentation.main.MainContract
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

  override fun startBackPressedFlagResetTimer() {
    Handler().postDelayed({ presenter?.setBackPressedFlagToFalse() }, 2000)
  }

  override fun showPreviousFragment() {
    supportFragmentManager.popBackStack()
  }

  override fun getFragmentTagAtStackTop(): String {
    return supportFragmentManager
        .getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
  }

  override fun getExploreFragmentTag(): String {
    return WallpaperFragment.EXPLORE_FRAGMENT_TAG
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
    return getFragmentTagAtStackTop() == fragmentTag
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

  private fun buildGuillotineMenuItems(): List<Triple<Int, Int, MenuItems>> {
    // Declare mutable list containing names and icon resources of guillotine menu items
    val menuItemDetails = mutableListOf<Triple<Int, Int, MenuItems>>().apply {
      add(Triple(R.string.guillotine_explore_title, R.drawable.ic_explore_white,
          MenuItems.EXPLORE))
      add(Triple(R.string.guillotine_top_picks_title, R.drawable.ic_toppicks_white,
          MenuItems.TOP_PICKS))
      add(Triple(R.string.guillotine_categories_title, R.drawable.ic_categories_white,
          MenuItems.CATEGORIES))
      add(Triple(R.string.guillotine_minimal_title, R.drawable.ic_minimal_white,
          MenuItems.MINIMAL))
      add(Triple(R.string.guillotine_collection_title, R.drawable.ic_collections_white,
          MenuItems.COLLECTION))
      add(Triple(R.string.guillotine_feedback_title, R.drawable.ic_feedback_white,
          MenuItems.FEEDBACK))
      add(Triple(R.string.guillotine_buy_pro_title, R.drawable.ic_buypro_black,
          MenuItems.BUY_PRO))
    }
    return menuItemDetails
  }

  private fun setUpGuillotineMenuItems(guillotineMenuItems: List<Triple<Int, Int, MenuItems>>) {
    // Programmatically add guillotine menu items
    val layoutInflater = LayoutInflater.from(this)
    val itemIterator = guillotineMenuItems.iterator()
    itemIterator.forEach {
      val guillotineMenuItemView = layoutInflater
          .inflate(R.layout.item_guillotine_menu, null)
      rootLinearLayoutGuillotineMenu?.addView(guillotineMenuItemView)
      with(guillotineMenuItemView) {
        id = it.first
        textviewGuillotineMenuItem.text = stringRes(it.first)
        imageviewGuillotineMenuItem.setImageDrawable(drawableRes(it.second))
      }
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

  private fun clickListener(item: MenuItems) {
    when (item) {
      MenuItems.EXPLORE -> addFragment(fragmentContainer.id,
          WallpaperFragment.newInstance(), WallpaperFragment.EXPLORE_FRAGMENT_TAG)
      MenuItems.TOP_PICKS -> addFragment(fragmentContainer.id, WallpaperFragment.newInstance(),
          WallpaperFragment.TOP_PICKS_FRAGMENT_TAG)
      MenuItems.CATEGORIES -> addFragment(fragmentContainer.id, WallpaperFragment.newInstance(),
          WallpaperFragment.CATEGORIES_FRAGMENT_TAG)
      MenuItems.MINIMAL -> addFragment(fragmentContainer.id,
          MinimalFragment.newInstance(), MinimalFragment.MINIMAL_FRAGMENT_TAG)
      MenuItems.COLLECTION -> addFragment(fragmentContainer.id,
          CollectionFragment.newInstance(), CollectionFragment.COLLECTION_FRAGMENT_TAG)
      MenuItems.FEEDBACK -> {
        // TODO : Add feedback implementation
      }
      MenuItems.BUY_PRO -> {
        withDelayOnMain(550, block = {
          startActivity(Intent(this, BuyProActivity::class.java))
        }
        )
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

  private enum class MenuItems {
    EXPLORE,
    TOP_PICKS,
    CATEGORIES,
    MINIMAL,
    COLLECTION,
    FEEDBACK,
    BUY_PRO
  }

}