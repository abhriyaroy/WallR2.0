package zebrostudio.wallr100.android.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.ImageView
import android.widget.LinearLayout
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.zebrostudio.wallrcustoms.customtextview.WallrCustomTextView
import dagger.android.support.AndroidSupportInjection
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.EXPLORE_TAG
import zebrostudio.wallr100.android.utils.checkDataConnection
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.setMenuItemColorRed
import zebrostudio.wallr100.android.utils.setMenuItemColorWhite
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.BaseView
import javax.inject.Inject

abstract class BaseFragment : Fragment(), BaseView {

  @Inject lateinit var fragmentNameTagFetcherImpl: FragmentNameTagFetcher
  internal var fragmentTag: String = EXPLORE_TAG

  private val menuItemIdList: List<Int> = listOf(
      R.string.explore_fragment_tag,
      R.string.top_picks_fragment_tag,
      R.string.categories_fragment_tag,
      R.string.minimal_fragment_tag,
      R.string.collection_fragment_tag
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

  private fun showToolbarMenuIcon() {
    activity?.findViewById<ImageView>(R.id.toolbarMultiSelectIcon)?.gone()
    activity?.findViewById<ImageView>(R.id.toolbarSearchIcon)?.gone()
    when (fragmentTag) {
      getString(R.string.minimal_fragment_tag) ->
        activity?.findViewById<ImageView>(R.id.toolbarMultiSelectIcon)?.visible()
      getString(R.string.collection_fragment_tag) -> {  // Do nothing
      }
      else -> activity?.findViewById<ImageView>(R.id.toolbarSearchIcon)?.visible()
    }
  }

  override fun getScope(): ScopeProvider {
    return AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
  }

  override fun internetAvailability() = activity?.checkDataConnection()!!

}