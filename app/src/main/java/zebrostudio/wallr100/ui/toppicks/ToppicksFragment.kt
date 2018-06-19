package zebrostudio.wallr100.ui.toppicks

import zebrostudio.wallr100.ui.basefragment.BaseFragment
import javax.inject.Inject

class ToppicksFragment : BaseFragment(), ToppicksContract.TopicksView {

  @Inject
  internal lateinit var presenter: ToppicksPresenterImpl

  companion object {
    val TOPPICKS_FRAGMENT_TAG = "Top Picks"

    fun newInstance(): ToppicksFragment {
      return ToppicksFragment()
    }
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
    presenter.updateFragmentName(TOPPICKS_FRAGMENT_TAG)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

}