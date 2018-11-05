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
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.visible

class WallpaperFragment : BaseFragment() {

  private val exploreTabTitle = "EXPLORE"
  private val recentTabTitle = "RECENT"
  private val popularTabTitle = "POPULAR"
  private val standoutTabTitle = "STANDOUTS"
  private val buildingsTabTitle = "BUILDINGS"
  private val foodTabTitle = "FOOD"
  private val natureTabTitle = "NATURE"
  private val objectsTabTitle = "OBJECTS"
  private val peopleTabTitle = "PEOPLE"
  private val technologyTabTitle = "TECHNOLOGY"

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
    val imageListFragmentClass = ImageListFragment().javaClass
    if (fragmentTag == EXPLORE_FRAGMENT_TAG) {
      configureExploreChildView(imageListFragmentClass)
    } else if (fragmentTag == TOP_PICKS_FRAGMENT_TAG) {
      configureTopPicksChildView(tabLayout, imageListFragmentClass)
    } else {
      configureCategoriesChildView(tabLayout, imageListFragmentClass)
    }
  }

  private fun configureExploreChildView(imageListFragmentClass: Class<ImageListFragment>) {
    val viewPagerItemAdapter =
        FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(context)
            .add(exploreTabTitle, imageListFragmentClass)
            .create())
    wallpaperFragmentViewPager.adapter = viewPagerItemAdapter
  }

  private fun configureTopPicksChildView(
    tabLayout: SmartTabLayout?,
    imageListFragmentClass: Class<ImageListFragment>
  ) {
    val viewPagerItemAdapter =
        FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(context)
            .add(recentTabTitle, imageListFragmentClass)
            .add(popularTabTitle, imageListFragmentClass)
            .add(standoutTabTitle, imageListFragmentClass)
            .create())
    wallpaperFragmentViewPager.adapter = viewPagerItemAdapter
    tabLayout?.setViewPager(wallpaperFragmentViewPager)
    tabLayout?.visible()
  }

  private fun configureCategoriesChildView(
    tabLayout: SmartTabLayout?,
    imageListFragmentClass: Class<ImageListFragment>
  ) {
    val viewPagerItemAdapter =
        FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(context)
            .add(buildingsTabTitle, imageListFragmentClass)
            .add(foodTabTitle, imageListFragmentClass)
            .add(natureTabTitle, imageListFragmentClass)
            .add(objectsTabTitle, imageListFragmentClass)
            .add(peopleTabTitle, imageListFragmentClass)
            .add(technologyTabTitle, imageListFragmentClass)
            .create())
    wallpaperFragmentViewPager.adapter = viewPagerItemAdapter
    tabLayout?.setViewPager(wallpaperFragmentViewPager)
    tabLayout?.visible()
  }

  companion object {
    const val EXPLORE_FRAGMENT_TAG = "Explore"
    const val TOP_PICKS_FRAGMENT_TAG = "Top Picks"
    const val CATEGORIES_FRAGMENT_TAG = "Categories"

    fun newInstance() = WallpaperFragment()
  }

}