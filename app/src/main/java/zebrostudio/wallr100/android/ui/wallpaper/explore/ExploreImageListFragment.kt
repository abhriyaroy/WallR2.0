package zebrostudio.wallr100.android.ui.wallpaper.explore

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.wallpaper.BaseImageListFragment
import zebrostudio.wallr100.presentation.wallpaper.explore.ExploreContract
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
}
