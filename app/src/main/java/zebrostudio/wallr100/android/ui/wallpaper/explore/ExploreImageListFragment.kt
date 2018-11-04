package zebrostudio.wallr100.android.ui.wallpaper.explore

import zebrostudio.wallr100.android.ui.wallpaper.BaseImageListFragment
import zebrostudio.wallr100.presentation.wallpaper.explore.ExploreContract
import javax.inject.Inject

class ExploreImageListFragment : BaseImageListFragment(), ExploreContract.ExploreView {
  @Inject lateinit var explorePresenter: ExploreContract.ExplorePresenter

  override fun onResume() {
    super.onResume()
    explorePresenter.attachView(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    explorePresenter.detachView()
  }

  override fun initializePresenter() {

  }

  fun abc(a: Int) {
    System.out.println("heeeere" + a)
  }
}
