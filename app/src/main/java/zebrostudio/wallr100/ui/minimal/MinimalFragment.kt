package zebrostudio.wallr100.ui.minimal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zebrostudio.wallr100.R
import zebrostudio.wallr100.ui.basefragment.BaseFragment
import javax.inject.Inject

class MinimalFragment : BaseFragment(), MinimalContract.MinimalView {

  @Inject
  internal lateinit var presenter: MinimalContract.MinimalPresenter

  companion object {
    val MINIMAL_FRAGMENT_TAG = "Minimal"

    fun newInstance(): MinimalFragment {
      return MinimalFragment()
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_layout, container, false)
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

}