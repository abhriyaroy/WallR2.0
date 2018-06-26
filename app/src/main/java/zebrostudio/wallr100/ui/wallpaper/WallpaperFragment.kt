package zebrostudio.wallr100.ui.wallpaper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zebrostudio.wallr100.R
import zebrostudio.wallr100.ui.basefragment.BaseFragment
import zebrostudio.wallr100.utils.inflate
import javax.inject.Inject

@Suppress("NOTHING_TO_INLINE")
class WallpaperFragment : BaseFragment(), WallpaperContract.WallpaperView {

  @Inject
  internal lateinit var presenter: WallpaperContract.WallpaperPresenter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return container?.inflate(inflater, R.layout.fragment_wallpaper)
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  companion object {
    const val EXPLORE_TAG = "Explore"
    const val TOP_PICKS_TAG = "Top Picks"
    const val CATEGORIES_TAG = "Categories"

    inline fun newInstance(): WallpaperFragment {
      return WallpaperFragment()
    }
  }

}