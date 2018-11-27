package zebrostudio.wallr100.android.ui.wallpaper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ogaclejapan.smarttablayout.SmartTabLayout
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_wallpaper.wallpaperFragmentViewPager
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.utils.FragmentNameTag
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.visible
import javax.inject.Inject

class WallpaperFragment : BaseFragment() {

  @Inject lateinit var fragmentNameTags: FragmentNameTag

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidSupportInjection.inject(this)
  }

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
    if (fragmentTag == fragmentNameTags.getTag(R.string.explore_fragment_tag)) {
      configureExploreChildView(imageListFragmentClass)
    } else if (fragmentTag == fragmentNameTags.getTag(R.string.top_picks_fragment_tag)) {
      configureTopPicksChildView(tabLayout, imageListFragmentClass)
    } else {
      configureCategoriesChildView(tabLayout, imageListFragmentClass)
    }
  }

  private fun configureExploreChildView(imageListFragmentClass: Class<ImageListFragment>) {
    val viewPagerItemAdapter =
        FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(context)
            .add(getString(R.string.explore_images), imageListFragmentClass)
            .create())
    wallpaperFragmentViewPager.adapter = viewPagerItemAdapter
  }

  private fun configureTopPicksChildView(
    tabLayout: SmartTabLayout?,
    imageListFragmentClass: Class<ImageListFragment>
  ) {
    val viewPagerItemAdapter =
        FragmentPagerItemAdapter(childFragmentManager, FragmentPagerItems.with(context)
            .add(getString(R.string.recent_images), imageListFragmentClass)
            .add(getString(R.string.popular_images), imageListFragmentClass)
            .add(getString(R.string.standouts_images), imageListFragmentClass)
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
            .add(getString(R.string.buildings_images), imageListFragmentClass)
            .add(getString(R.string.food_images), imageListFragmentClass)
            .add(getString(R.string.nature_images), imageListFragmentClass)
            .add(getString(R.string.objects_images), imageListFragmentClass)
            .add(getString(R.string.people_images), imageListFragmentClass)
            .add(getString(R.string.technology_images), imageListFragmentClass)
            .create())
    wallpaperFragmentViewPager.adapter = viewPagerItemAdapter
    tabLayout?.setViewPager(wallpaperFragmentViewPager)
    tabLayout?.visible()
  }

  companion object {
    fun newInstance() = WallpaperFragment()
  }

}