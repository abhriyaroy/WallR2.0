package zebrostudio.wallr100.android.ui.minimal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.presentation.minimal.MinimalContract
import javax.inject.Inject

class MinimalFragment : BaseFragment(), MinimalContract.MinimalView {

  @Inject
  internal lateinit var presenter: MinimalContract.MinimalPresenter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return container?.inflate(inflater, R.layout.fragment_minimal)
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
    const val MINIMAL_FRAGMENT_TAG = "Minimal"

    fun newInstance() = MinimalFragment()
  }

}