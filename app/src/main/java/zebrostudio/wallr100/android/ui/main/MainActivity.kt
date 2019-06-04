package zebrostudio.wallr100.android.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.afollestad.materialcab.MaterialCab
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.yalantis.guillotine.animation.GuillotineAnimation
import com.yalantis.guillotine.interfaces.GuillotineListener
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_main.appbar
import kotlinx.android.synthetic.main.activity_main.fragmentContainer
import kotlinx.android.synthetic.main.activity_main.rootFrameLayout
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.item_guillotine_menu.view.imageviewGuillotineMenuItem
import kotlinx.android.synthetic.main.item_guillotine_menu.view.textviewGuillotineMenuItem
import kotlinx.android.synthetic.main.menu_guillotine_layout.rootLinearLayoutGuillotineMenu
import kotlinx.android.synthetic.main.menu_guillotine_layout.view.hamburgerGuillotineMenu
import kotlinx.android.synthetic.main.menu_guillotine_layout.view.proBadgeGuillotineMenu
import kotlinx.android.synthetic.main.toolbar_layout.contentHamburger
import kotlinx.android.synthetic.main.toolbar_layout.toolbarSearchIcon
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig
import zebrostudio.wallr100.android.ui.collection.CollectionFragment
import zebrostudio.wallr100.android.ui.minimal.MinimalFragment
import zebrostudio.wallr100.android.ui.search.SearchActivity
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher
import zebrostudio.wallr100.android.utils.FragmentTag
import zebrostudio.wallr100.android.utils.FragmentTag.CATEGORIES_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.COLLECTIONS_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.EXPLORE_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.MINIMAL_TAG
import zebrostudio.wallr100.android.utils.FragmentTag.TOP_PICKS_TAG
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.drawableRes
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.infoToast
import zebrostudio.wallr100.android.utils.menuTitleToast
import zebrostudio.wallr100.android.utils.setOnDebouncedClickListener
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.withDelayOnMain
import zebrostudio.wallr100.presentation.main.MainContract
import zebrostudio.wallr100.presentation.main.MainContract.MainView
import javax.inject.Inject

private const val RIPPLE_DURATION: Long = 250
private const val MAIL_URI = "mailto:"

class MainActivity : AppCompatActivity(), MainView, HasSupportFragmentInjector {

  @Inject
  internal lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>
  @Inject
  internal lateinit var presenter: MainContract.MainPresenter
  @Inject
  internal lateinit var fragmentNameTagFetcher: FragmentNameTagFetcher

  private lateinit var guillotineMenuAnimation: GuillotineAnimation

  private var buyProMenuItem: View? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    presenter.attachView(this)
    setContentView(R.layout.activity_main)
    initializeViews()
    addFragment(fragmentContainer.id, WallpaperFragment.newInstance(), EXPLORE_TAG)

    attachToolbarItemClickListeners()
    presenter.handleViewCreated()
  }

  override fun onBackPressed() {
    presenter.handleBackPress()
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (resultCode == PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE) {
      buyProMenuItem?.gone()
    }
  }

  override fun supportFragmentInjector() = fragmentDispatchingAndroidInjector

  override fun showHamburgerHint() {
    TapTargetView.showFor(this,
        TapTarget.forView(findViewById(R.id.contentHamburger),
            stringRes(R.string.main_activity_hamburger_hint_title),
            stringRes(R.string.main_activity_hamburger_hint_description))
            .dimColor(android.R.color.transparent)
            .outerCircleColor(R.color.accent)
            .targetCircleColor(R.color.tap_target_hint_inner_circle)
            .textColor(android.R.color.white)
            .cancelable(true),
        object : TapTargetView.Listener() {
          override fun onTargetClick(view: TapTargetView) {
            super.onTargetClick(view)
            guillotineMenuAnimation.open()
            presenter.handleHamburgerHintDismissed()
          }

          override fun onTargetDismissed(view: TapTargetView?, userInitiated: Boolean) {
            super.onTargetDismissed(view, userInitiated)
            presenter.handleHamburgerHintDismissed()
          }

          override fun onOuterCircleClick(view: TapTargetView?) {
            super.onTargetClick(view!!)
            view.dismiss(true)
          }
        })
  }

  override fun exitApp() {
    this.finish()
  }

  override fun showExitConfirmation() {
    infoToast(stringRes(R.string.main_activity_exit_confirmation_message), Toast.LENGTH_SHORT)
  }

  override fun closeNavigationMenu() {
    guillotineMenuAnimation.close()
  }

  override fun startBackPressedFlagResetTimer() {
    withDelayOnMain(2000) { presenter.setBackPressedFlagToFalse() }
  }

  override fun showPreviousFragment() {
    supportFragmentManager.popBackStack()
  }

  override fun getFragmentTagAtStackTop(): FragmentTag {
    return when (supportFragmentManager
        .getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name!!) {
      EXPLORE_TAG.toString() -> EXPLORE_TAG
      TOP_PICKS_TAG.toString() -> TOP_PICKS_TAG
      CATEGORIES_TAG.toString() -> CATEGORIES_TAG
      MINIMAL_TAG.toString() -> MINIMAL_TAG
      else -> COLLECTIONS_TAG
    }
  }

  override fun isCabActive(): Boolean {
    return MaterialCab.isActive
  }

  override fun dismissCab() {
    MaterialCab.destroy()
  }

  override fun showAppBar() {
    appbar.setExpanded(true, true)
  }

  override fun showOperationInProgressMessage() {
    infoToast(stringRes(R.string.finalizing_stuff_wait_message), Toast.LENGTH_SHORT)
  }

  override fun isOperationActive(): Boolean {
    return isOperationInProcess
  }

  override fun showFeedbackClient(
    emailSubject: String,
    emailAddress: Array<String>,
    emailIntentType: String
  ) {
    closeNavigationMenu()
    withDelayOnMain(100) {
      Intent(Intent.ACTION_SENDTO).apply {
        type = emailIntentType
        data = Uri.parse(MAIL_URI)
        putExtra(Intent.EXTRA_EMAIL, emailAddress)
        putExtra(Intent.EXTRA_SUBJECT, emailSubject)
      }.let {
        startActivityForResult(Intent.createChooser(it,
            stringRes(R.string.main_activity_feedback_contact_using_message)), 0)
      }
    }
  }

  private inline fun <reified T : BaseFragment> addFragment(
    @IdRes id: Int,
    fragment: T,
    fragmentTag: FragmentTag
  ) {
    if (!fragmentExistsOnStackTop(fragmentTag)) {
      if (fragmentTag == EXPLORE_TAG) {
        clearStack()
      }

      supportFragmentManager
          .beginTransaction()
          .replace(id, fragment, fragmentTag.toString())
          .addToBackStack(fragmentTag.toString())
          .commitAllowingStateLoss()

      fragment.fragmentTag = fragmentTag
    }
  }

  private fun fragmentExistsOnStackTop(fragmentTag: FragmentTag): Boolean {
    if (supportFragmentManager.backStackEntryCount == 0)
      return false
    return getFragmentTagAtStackTop() == fragmentTag
  }

  private fun initializeViews() {
    val guillotineMenu = LayoutInflater.from(this)
        .inflate(R.layout.menu_guillotine_layout, null)
    rootFrameLayout.addView(guillotineMenu)

    setSupportActionBar(toolbar)

    val guillotineListener = object : GuillotineListener {
      override fun onGuillotineOpened() {
        presenter.handleNavigationMenuOpened()
      }

      override fun onGuillotineClosed() {
        presenter.handleNavigationMenuClosed()
      }
    }

    guillotineMenuAnimation = GuillotineAnimation.GuillotineBuilder(
        guillotineMenu,
        guillotineMenu.hamburgerGuillotineMenu,
        contentHamburger)
        .setStartDelay(RIPPLE_DURATION)
        .setActionBarViewForAnimation(toolbar)
        .setGuillotineListener(guillotineListener)
        .setClosedOnStart(true)
        .build()

    setUpGuillotineMenuItems(buildGuillotineMenuItems())
  }

  private fun buildGuillotineMenuItems(): List<Triple<Int, Int, MenuItems>> {
    // Declare mutable list containing names and icon resources of guillotine menu items
    return mutableListOf<Triple<Int, Int, MenuItems>>().apply {
      add(Triple(R.string.explore_fragment_tag, R.drawable.ic_explore_white,
          MenuItems.EXPLORE))
      add(Triple(R.string.top_picks_fragment_tag, R.drawable.ic_toppicks_white,
          MenuItems.TOP_PICKS))
      add(Triple(R.string.categories_fragment_tag, R.drawable.ic_categories_white,
          MenuItems.CATEGORIES))
      add(Triple(R.string.minimal_fragment_tag, R.drawable.ic_minimal_white,
          MenuItems.MINIMAL))
      add(Triple(R.string.collection_fragment_tag, R.drawable.ic_collections_white,
          MenuItems.COLLECTION))
      add(Triple(R.string.feedback_fragment_tag, R.drawable.ic_feedback_white,
          MenuItems.FEEDBACK))
      add(Triple(R.string.guillotine_buy_pro_title, R.drawable.ic_buypro_black,
          MenuItems.BUY_PRO))
    }
  }

  private fun setUpGuillotineMenuItems(guillotineMenuItems: List<Triple<Int, Int, MenuItems>>) {
    // Programmatically add guillotine menu items
    val layoutInflater = LayoutInflater.from(this)
    val itemIterator = guillotineMenuItems.iterator()
    itemIterator.forEach {
      val guillotineMenuItemView = layoutInflater
          .inflate(R.layout.item_guillotine_menu, null)
      rootLinearLayoutGuillotineMenu?.addView(guillotineMenuItemView)
      if (presenter.shouldShowPurchaseOption()) {
        rootLinearLayoutGuillotineMenu.proBadgeGuillotineMenu.gone()
      }
      with(guillotineMenuItemView) {
        id = it.first
        textviewGuillotineMenuItem.text = stringRes(it.first)
        imageviewGuillotineMenuItem.setImageDrawable(drawableRes(it.second))
      }
      // Make the background white and text color black for the buy pro guillotine menu item
      if (!itemIterator.hasNext()) {
        guillotineMenuItemView.apply {
          setBackgroundColor(colorRes(R.color.white))
          textviewGuillotineMenuItem
              .setTextColor(colorRes(R.color.black))
          if (!presenter.shouldShowPurchaseOption()) {
            gone()
          }
        }
        buyProMenuItem = guillotineMenuItemView
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
          WallpaperFragment.newInstance(),
          EXPLORE_TAG)
      MenuItems.TOP_PICKS -> addFragment(fragmentContainer.id, WallpaperFragment.newInstance(),
          TOP_PICKS_TAG)
      MenuItems.CATEGORIES -> addFragment(fragmentContainer.id, WallpaperFragment.newInstance(),
          CATEGORIES_TAG)
      MenuItems.MINIMAL -> addFragment(fragmentContainer.id,
          MinimalFragment.newInstance(),
          MINIMAL_TAG)
      MenuItems.COLLECTION -> addFragment(fragmentContainer.id,
          CollectionFragment.newInstance(),
          COLLECTIONS_TAG)
      MenuItems.FEEDBACK -> presenter.handleFeedbackMenuItemClick()
      MenuItems.BUY_PRO -> {
        withDelayOnMain(550, block = {
          startActivityForResult(Intent(this, BuyProActivity::class.java),
              PurchaseTransactionConfig.PURCHASE_REQUEST_CODE)
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

  private fun attachToolbarItemClickListeners() {
    toolbarSearchIcon.let { searchIcon ->
      searchIcon.setOnClickListener {
        val searchActivityIntent = Intent(this, SearchActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, it,
              stringRes(R.string.search_view_transition_name))
          startActivity(searchActivityIntent, options.toBundle())
        } else {
          startActivity(searchActivityIntent)
        }
      }

      searchIcon.setOnLongClickListener { view ->
        view.menuTitleToast(this,
            stringRes(R.string.minimal_fragment_toolbar_menu_multiselect_title),
            window)
        true
      }
    }
  }

  companion object {
    private var isOperationInProcess = false

    fun blockBackPress() {
      isOperationInProcess = true
    }

    fun releaseBackPressBlock() {
      isOperationInProcess = false
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