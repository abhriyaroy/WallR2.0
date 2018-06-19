package zebrostudio.wallr100.ui.explore

import zebrostudio.wallr100.ui.basefragment.BaseFragment
import javax.inject.Inject

class ExploreFragment : BaseFragment(), ExploreContract.ExploreView {

  @Inject
  internal lateinit var presenter: ExplorePresenterImpl

  companion object {
    val EXPLORE_FRAGMENT_TAG = "Explore"

    fun newInstance(): ExploreFragment {
      return ExploreFragment()
    }
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
    presenter.updateFragmentName(EXPLORE_FRAGMENT_TAG)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

}