package zebrostudio.wallr100.ui.main

import android.os.Bundle
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
import zebrostudio.wallr100.ui.categories.CategoriesFragment
import zebrostudio.wallr100.ui.explore.ExploreFragment
import zebrostudio.wallr100.ui.minimal.MinimalFragment
import zebrostudio.wallr100.ui.toppicks.ToppicksFragment
import zebrostudio.wallr100.utils.colorRes
import zebrostudio.wallr100.utils.drawableRes
import zebrostudio.wallr100.utils.infoToast
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
    showExploreFragment()
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
    infoToast(stringRes(R.string.exit_toast))
  }

  override fun closeGuillotineMenu() {
    guillotineMenuAnimation.close()
  }

  override fun showPreviousFragment() {
    supportFragmentManager.popBackStack()
  }

  override fun showExploreFragment() {
    supportFragmentManager
        .beginTransaction()
        .replace(fragmentContainer.id, ExploreFragment.newInstance(),
            ExploreFragment.EXPLORE_FRAGMENT_TAG)
        .addToBackStack(null)
        .commit()
  }

  override fun showTopPicksFragment() {
    supportFragmentManager
        .beginTransaction()
        .replace(fragmentContainer.id, ToppicksFragment.newInstance(),
            ToppicksFragment.TOPPICKS_FRAGMENT_TAG)
        .addToBackStack(null)
        .commit()
  }

  override fun showCategorisFragment() {
    supportFragmentManager
        .beginTransaction()
        .replace(fragmentContainer.id, CategoriesFragment.newInstance(),
            CategoriesFragment.CATEGORIES_FRAGMENT_TAG)
        .addToBackStack(null)
        .commit()
  }

  override fun showMinimalFragment() {
    supportFragmentManager
        .beginTransaction()
        .replace(fragmentContainer.id, MinimalFragment.newInstance(),
            MinimalFragment.MINIMAL_FRAGMENT_TAG)
        .addToBackStack(null)
        .commit()
  }

  override fun showCollectionFragment() {

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

  private fun buildGuillotineMenuItems(): List<Pair<Int, Int>> {
    // Declare mutable list containing names and icon resources of guillotine menu items
    val menuItemDetails = mutableListOf<Pair<Int, Int>>()
    menuItemDetails.add(R.string.guillotine_explore_title to R.drawable.ic_explore_white)
    menuItemDetails.add(R.string.guillotine_toppicks_title to R.drawable.ic_toppicks_white)
    menuItemDetails.add(R.string.guillotine_categories_title to R.drawable.ic_categories_white)
    menuItemDetails.add(R.string.guillotine_minimal_title to R.drawable.ic_minimal_white)
    menuItemDetails.add(R.string.guillotine_collection_title to R.drawable.ic_collections_white)
    menuItemDetails.add(R.string.guillotine_feedback_title to R.drawable.ic_feedback_white)
    menuItemDetails.add(R.string.guillotine_buypro_title to R.drawable.ic_buypro_black)
    return menuItemDetails
  }

  private fun setUpGuillotineMenuItems(guillotineMenuItems: List<Pair<Int, Int>>) {
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
      guillotineMenuItemView.setOnClickListener {
        clickListener(guillotineMenuItemView.id)
      }
    }
  }

  private fun clickListener(id: Int) {
    when (id) {
      R.string.guillotine_explore_title -> showExploreFragment()
      R.string.guillotine_toppicks_title -> showTopPicksFragment()
      R.string.guillotine_categories_title -> showCategorisFragment()
      R.string.guillotine_minimal_title -> showMinimalFragment()
      R.string.guillotine_collection_title -> showCollectionFragment()

    }
  }

}