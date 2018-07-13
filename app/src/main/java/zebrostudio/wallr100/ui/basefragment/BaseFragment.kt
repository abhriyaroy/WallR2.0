package zebrostudio.wallr100.ui.basefragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.zebrostudio.wallrcustoms.customtextview.WallrCustomTextView
import dagger.android.support.AndroidSupportInjection
import zebrostudio.wallr100.R
import zebrostudio.wallr100.utils.setMenuItemColorRed
import zebrostudio.wallr100.utils.setMenuItemColorWhite

abstract class BaseFragment : Fragment() {

  internal lateinit var fragmentTag: String

  private val menuItemIdList: List<Int> = listOf(
      R.string.guillotine_explore_title,
      R.string.guillotine_top_picks_title,
      R.string.guillotine_categories_title,
      R.string.guillotine_minimal_title,
      R.string.guillotine_collection_title
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidSupportInjection.inject(this)
  }

  @SuppressLint("ResourceType")
  override fun onResume() {
    super.onResume()
    activity?.findViewById<WallrCustomTextView>(R.id.toolbarTitle)?.text = fragmentTag

    highlightCurrentMenuItem()
    showAppropriateToolbarMenuIcon()

  }

  private fun highlightCurrentMenuItem() {
    for (menuItem in menuItemIdList) {
      if (getString(menuItem) == fragmentTag) {
        activity?.findViewById<LinearLayout>(menuItem)?.setMenuItemColorRed(this.context!!)
      } else {
        activity?.findViewById<LinearLayout>(menuItem)?.setMenuItemColorWhite(this.context!!)
      }
    }
  }

  private fun showAppropriateToolbarMenuIcon() {
    activity?.findViewById<ImageView>(R.id.toolbarMultiSelectIcon)?.visibility = View.GONE
    activity?.findViewById<ImageView>(R.id.toolbarSearchIcon)?.visibility = View.GONE
    when (fragmentTag) {
      getString(R.string.guillotine_minimal_title) ->
        activity?.findViewById<ImageView>(R.id.toolbarMultiSelectIcon)?.visibility = View.VISIBLE
      getString(R.string.guillotine_collection_title) -> {  // Do nothing
      }
      else -> activity?.findViewById<ImageView>(R.id.toolbarSearchIcon)?.visibility = View.VISIBLE
    }
  }

}