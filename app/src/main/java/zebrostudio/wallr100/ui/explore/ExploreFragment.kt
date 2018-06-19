package zebrostudio.wallr100.ui.explore

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import zebrostudio.wallr100.R
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
    Log.d(EXPLORE_FRAGMENT_TAG, "resume called")
    presenter.attachView(this)
    presenter.updateFragmentName(EXPLORE_FRAGMENT_TAG)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

}