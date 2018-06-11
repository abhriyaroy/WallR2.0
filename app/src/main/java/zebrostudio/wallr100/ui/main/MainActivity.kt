package zebrostudio.wallr100.ui.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Pair
import android.view.LayoutInflater
import com.yalantis.guillotine.animation.GuillotineAnimation
import com.yalantis.guillotine.interfaces.GuillotineListener
import kotlinx.android.synthetic.main.activity_main.root_frame_layout
import kotlinx.android.synthetic.main.activity_main.toolbar
import kotlinx.android.synthetic.main.guillotine_menu_layout.root_linear_layout_guillotine_menu
import kotlinx.android.synthetic.main.item_guillotine_menu.view.imageview_guillotine_menu_item
import kotlinx.android.synthetic.main.item_guillotine_menu.view.textview_guillotine_menu_item
import kotlinx.android.synthetic.main.toolbar_layout.content_hamburger
import zebrostudio.wallr100.R

class MainActivity : AppCompatActivity() {

  private var isGuillotineMenuOpen = false
  private var guillotineMenuAnimation: GuillotineAnimation? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    initializeViews()
  }

  override fun onBackPressed() {
    if (isGuillotineMenuOpen) {
      guillotineMenuAnimation?.close()
    } else {
      super.onBackPressed()
    }
  }

  private fun initializeViews() {
    val guillotineMenu = LayoutInflater.from(this)
        .inflate(R.layout.guillotine_menu_layout, null)
    root_frame_layout.addView(guillotineMenu)

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
        guillotineMenu.findViewById(R.id.hamburger_guillotine_menu),
        content_hamburger)
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
    menuItemDetails.add(
        Pair(R.string.guillotine_explore_title, R.drawable.ic_explore_white))
    menuItemDetails.add(
        Pair(R.string.guillotine_toppicks_title, R.drawable.ic_toppicks_white))
    menuItemDetails.add(
        Pair(R.string.guillotine_categories_title, R.drawable.ic_categories_white))
    menuItemDetails.add(
        Pair(R.string.guillotine_minimal_title, R.drawable.ic_minimal_white))
    menuItemDetails.add(
        Pair(R.string.guillotine_collection_title, R.drawable.ic_collections_white))
    menuItemDetails.add(
        Pair(R.string.guillotine_feedback_title, R.drawable.ic_feedback_white))
    menuItemDetails.add(
        Pair(R.string.guillotine_buypro_title, R.drawable.ic_buypro_black))
    return menuItemDetails
  }

  private fun setUpGuillotineMenuItems(guillotineMenuItems: List<Pair<Int, Int>>) {
    // Programmatically add guillotine menu items
    val layoutInflater = LayoutInflater.from(this)
    for (i in guillotineMenuItems.indices) {
      val guillotineMenuItemView = layoutInflater
          .inflate(R.layout.item_guillotine_menu, null)
      root_linear_layout_guillotine_menu?.addView(guillotineMenuItemView)
      guillotineMenuItemView.id = guillotineMenuItems[i].first
      guillotineMenuItemView.textview_guillotine_menu_item.text =
          getString(guillotineMenuItems[i].first)
      guillotineMenuItemView.imageview_guillotine_menu_item.setImageDrawable(
          resources.getDrawable(guillotineMenuItems[i].second))
      // Make the background white and text color black for the buy pro guillotine menu item
      if (i == guillotineMenuItems.size - 1) {
        guillotineMenuItemView.setBackgroundColor(resources.getColor(R.color.color_white))
        guillotineMenuItemView.textview_guillotine_menu_item
            .setTextColor(resources.getColor(R.color.color_black))
      }
      // Set on click listener
      guillotineMenuItemView.setOnClickListener {
        clickListener(it.id)
      }
    }
  }

  private fun clickListener(itemId : Int){
    when (itemId){
      R.string.guillotine_explore_title -> {

      }
    }
  }

}