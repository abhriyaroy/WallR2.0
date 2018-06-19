package zebrostudio.wallr100.ui.toppicks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zebrostudio.wallr100.R
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

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val rootView = inflater.inflate(R.layout.fragment_layout, container, false)
    return rootView
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