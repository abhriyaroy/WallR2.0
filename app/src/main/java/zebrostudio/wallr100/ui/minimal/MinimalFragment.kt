package zebrostudio.wallr100.ui.minimal

import zebrostudio.wallr100.ui.basefragment.BaseFragment
import javax.inject.Inject

class MinimalFragment : BaseFragment(), MinimalContract.MinimalView {

  @Inject
  internal lateinit var presenter: MinimalPresenterImpl

  companion object {
    val MINIMAL_FRAGMENT_TAG = "Minimal"

    fun newInstance(): MinimalFragment {
      return MinimalFragment()
    }
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
    presenter.updateFragmentName(MINIMAL_FRAGMENT_TAG)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

}