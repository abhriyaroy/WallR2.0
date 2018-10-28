package zebrostudio.wallr100.android.ui.wallpaper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract
import zebrostudio.wallr100.presentation.wallpaper.WallpaperContract
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import javax.inject.Inject

class WallpaperFragment : BaseFragment(), WallpaperContract.WallpaperView {

  @Inject
  internal lateinit var presenter: WallpaperContract.WallpaperPresenter
  @Inject
  internal lateinit var imageRecyclerViewPresenter: ImageRecyclerItemContract.ImageRecyclerViewPresenter

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

  override fun showLoader() {

  }

  override fun showImages(imagePresenterEntityList: List<ImagePresenterEntity>) {

  }

  override fun hideLoader() {

  }

  override fun hideAllLoadersAndMessageViews() {

  }

  override fun showGenericErrorMessageView() {

  }

  companion object {
    const val EXPLORE_FRAGMENT_TAG = "Explore"
    const val TOP_PICKS_FRAGMENT_TAG = "Top Picks"
    const val CATEGORIES_FRAGMENT_TAG = "Categories"

    fun newInstance() = WallpaperFragment()
  }

}