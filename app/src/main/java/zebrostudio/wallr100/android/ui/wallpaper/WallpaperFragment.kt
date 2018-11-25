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
            .add(ImageListType.imageType[0], imageListFragmentClass)
            .create())
    wallpaperFragmentViewPager.adapter = viewPagerItemAdapter
  }

  private fun configureTopPicksChildView(
    tabLayout: SmartTabLayout?,
    imageListFragmentClass: Class<ImageListFragment>
  ) {
    val viewPagerItemAdapter =
        FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(context)
            .add(ImageListType.imageType[1], imageListFragmentClass)
            .add(ImageListType.imageType[2], imageListFragmentClass)
            .add(ImageListType.imageType[3], imageListFragmentClass)
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
            .add(ImageListType.imageType[4], imageListFragmentClass)
            .add(ImageListType.imageType[5], imageListFragmentClass)
            .add(ImageListType.imageType[6], imageListFragmentClass)
            .add(ImageListType.imageType[7], imageListFragmentClass)
            .add(ImageListType.imageType[8], imageListFragmentClass)
            .add(ImageListType.imageType[9], imageListFragmentClass)
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