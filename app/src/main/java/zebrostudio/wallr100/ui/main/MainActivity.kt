package zebrostudio.wallr100.ui.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.widget.Toast
import com.yalantis.guillotine.animation.GuillotineAnimation
import com.yalantis.guillotine.interfaces.GuillotineListener
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.rootFrameLayout
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.guillotine_menu_layout.rootLinearLayoutGuillotineMenu
import kotlinx.android.synthetic.main.guillotine_menu_layout.view.hamburgerGuillotineMenu
import kotlinx.android.synthetic.main.item_guillotine_menu.view.imageviewGuillotineMenuItem
import kotlinx.android.synthetic.main.item_guillotine_menu.view.textviewGuillotineMenuItem
import kotlinx.android.synthetic.main.toolbar_layout.contentHamburger
import zebrostudio.wallr100.R
import zebrostudio.wallr100.utils.colorRes
import zebrostudio.wallr100.utils.drawableRes
import zebrostudio.wallr100.utils.stringRes
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainContract.MainView, HasSupportFragmentInjector {
  @Inject
  internal lateinit var presenter: MainContract.MainPresenter

  @Inject
  internal lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
  private var isGuillotineMenuOpen = false

  override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentDispatchingAndroidInjector

  private var guillotineMenuAnimation: GuillotineAnimation? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    presenter.attach(this)
    initializeViews()
  }

  override fun onBackPressed() {
    if (isGuillotineMenuOpen) {
      guillotineMenuAnimation?.close()
    } else {
      presenter.handleBackPress()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.detach()
  }

  override fun exitApp() {
    this.finish()
  }

  override fun showExitToast() {
    Toasty.info(this, getString(R.string.exit_toast), Toast.LENGTH_LONG, true).show()
  }

  override fun showPreviousFragment() {
    supportFragmentManager.popBackStack()
  }

  private fun initializeViews() {
    val guillotineMenu = LayoutInflater.from(this)
        .inflate(R.layout.guillotine_menu_layout, null)
    rootFrameLayout.addView(guillotineMenu)

    setSupportActionBar(toolbar)

    val guillotineListener = object : GuillotineListener {
      override fun onGuillotineOpened() {
        isGuillotineMenuOpen = true
      }

      override fun onGuillotineClosed() {
        isGuillotineMenuOpen = false
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
    }
  }

}