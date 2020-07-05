package zebrostudio.wallr100.android.ui

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import android.graphics.PorterDuff.Mode.MULTIPLY
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.getbase.floatingactionbutton.FloatingActionButton
import com.jaredrummler.materialspinner.MaterialSpinner
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.zebrostudio.wallrcustoms.customtextview.WallrCustomTextView
import dagger.android.support.AndroidSupportInjection
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.*
import zebrostudio.wallr100.android.utils.FragmentTag.*
import zebrostudio.wallr100.presentation.BaseView
import javax.inject.Inject
import kotlin.math.roundToInt

private const val COLLECTIONS_TITLE_PADDING = 48

abstract class BaseFragment : Fragment(), BaseView {

  @Inject
  lateinit var fragmentNameTagFetcherImpl: FragmentNameTagFetcher
  internal var fragmentTag: FragmentTag = EXPLORE_TAG

  private val menuItemIdList: List<Int> = listOf(
    R.string.explore_title,
    R.string.top_picks_title,
    R.string.categories_title,
    R.string.minimal_title,
    R.string.collection_title
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidSupportInjection.inject(this)
  }

  @SuppressLint("ResourceType")
  override fun onResume() {
    super.onResume()
    activity?.findViewById<WallrCustomTextView>(R.id.toolbarTitle)?.text =
        fragmentNameTagFetcherImpl.getFragmentName(fragmentTag)

    highlightCurrentMenuItem()
    showToolbarMenuIcon()
    configureTabs()
    adjustTitlePadding()
    configureWallpaperChangerLayoutVisibility()
    hideBottomLayout()
  }

  private fun highlightCurrentMenuItem() {
    for (itemPosition in 0 until menuItemIdList.size) {
      if (itemPosition == fragmentTag.ordinal) {
        highlightMenuItemView(activity!!.findViewById(menuItemIdList[itemPosition]))
      } else {
        clearMenuItemViewHighlight(activity!!.findViewById(menuItemIdList[itemPosition]))
      }
    }
  }

  private fun showToolbarMenuIcon() {
    activity?.let {
      it.findViewById<ImageView>(R.id.toolbarMultiSelectIcon)?.gone()
      it.findViewById<ImageView>(R.id.toolbarSearchIcon)?.gone()
      when (fragmentTag) {
        MINIMAL_TAG ->
          it.findViewById<ImageView>(R.id.toolbarMultiSelectIcon)?.visible()
        COLLECTIONS_TAG -> {  // Do nothing
        }
        else -> it.findViewById<ImageView>(R.id.toolbarSearchIcon)?.visible()
      }
    }
  }

  private fun configureTabs() {
    activity?.let {
      if (fragmentTag == CATEGORIES_TAG ||
          fragmentTag == TOP_PICKS_TAG) {
        it.findViewById<SmartTabLayout>(R.id.tabLayout)?.visible()
      } else {
        it.findViewById<SmartTabLayout>(R.id.tabLayout)?.gone()
      }
    }
  }

  private fun adjustTitlePadding() {
    activity?.let {
      if (fragmentTag == COLLECTIONS_TAG) {
        val dpAsPixels = (COLLECTIONS_TITLE_PADDING * resources.displayMetrics.density)
        it.findViewById<WallrCustomTextView>(R.id.toolbarTitle)
            ?.setPaddingRelative(dpAsPixels.roundToInt(), 0, 0, 0)
      } else {
        it.findViewById<WallrCustomTextView>(R.id.toolbarTitle)?.setPaddingRelative(0, 0, 0, 0)
      }
    }
  }

  private fun configureWallpaperChangerLayoutVisibility() {
    activity?.let {
      if (fragmentTag == COLLECTIONS_TAG) {
        it.findViewById<RelativeLayout>(R.id.switchLayout)?.visible()
      } else {
        it.findViewById<RelativeLayout>(R.id.switchLayout)?.gone()
      }
    }
  }

  private fun hideBottomLayout() {
    activity?.let {
      it.findViewById<RelativeLayout>(R.id.minimalBottomLayout)?.apply {
        invisible()
        isClickable = false
        isEnabled = false
      }
      it.findViewById<FloatingActionButton>(R.id.minimalBottomLayoutFab)?.apply{
        invisible()
      }
    }
  }

  override fun getScope(): ScopeProvider {
    return AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
  }

  override fun internetAvailability() = activity?.checkDataConnection()!!

  private fun highlightMenuItemView(menuItemView: LinearLayout) {
    menuItemView.findViewById<WallrCustomTextView>(R.id.textviewGuillotineMenuItem)
        ?.setTextColor(colorRes(R.color.accent))
    menuItemView.findViewById<ImageView>(R.id.imageviewGuillotineMenuItem)
        ?.setColorFilter(colorRes(R.color.accent), MULTIPLY)
  }

  private fun clearMenuItemViewHighlight(menuItemView: LinearLayout) {
    menuItemView.findViewById<WallrCustomTextView>(R.id.textviewGuillotineMenuItem)
        ?.setTextColor(colorRes(R.color.white))
    menuItemView.findViewById<ImageView>(R.id.imageviewGuillotineMenuItem)
        ?.setColorFilter(colorRes(R.color.white), MULTIPLY)
  }

}
