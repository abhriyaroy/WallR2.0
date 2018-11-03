package zebrostudio.wallr100.android.ui.wallpaper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.fragment_wallpaper.wallpaperFragmentViewPager
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.ui.wallpaper.explore.ExploreImageListFragment
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.visible

class WallpaperFragment : BaseFragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return container?.inflate(inflater, R.layout.fragment_wallpaper)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val tabLayout = activity?.findViewById<SmartTabLayout>(R.id.tabLayout)
    tabLayout?.gone()
    if (fragmentTag == EXPLORE_FRAGMENT_TAG) {
      val viewPagerItemAdapter =
          FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(context)
              .add("Explore", ExploreImageListFragment::class.java).create())
      wallpaperFragmentViewPager.adapter = viewPagerItemAdapter
    } else if (fragmentTag == TOP_PICKS_FRAGMENT_TAG) {
      val viewPagerItemAdapter =
          FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(context)
              .add("Explore", ExploreImageListFragment::class.java).create())
      wallpaperFragmentViewPager.adapter = viewPagerItemAdapter
      tabLayout?.setViewPager(wallpaperFragmentViewPager)
      tabLayout?.visible()
    } else {
      val viewPagerItemAdapter =
          FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(context)
              .add("Explore", ExploreImageListFragment::class.java).create())
      wallpaperFragmentViewPager.adapter = viewPagerItemAdapter
      tabLayout?.setViewPager(wallpaperFragmentViewPager)
      tabLayout?.visible()
    }
  }

  companion object {
    const val EXPLORE_FRAGMENT_TAG = "Explore"
    const val TOP_PICKS_FRAGMENT_TAG = "Top Picks"
    const val CATEGORIES_FRAGMENT_TAG = "Categories"

    fun newInstance() = WallpaperFragment()
  }

}