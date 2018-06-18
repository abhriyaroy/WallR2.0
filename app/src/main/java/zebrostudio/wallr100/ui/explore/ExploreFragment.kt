package zebrostudio.wallr100.ui.explore

import android.os.Bundle
import dagger.android.support.AndroidSupportInjection
import zebrostudio.wallr100.ui.basefragment.BaseFragment
import javax.inject.Inject

class ExploreFragment : BaseFragment() {

  @Inject
  internal lateinit var presenter: ExplorePresenterImpl

  companion object {
    val EXPLORE_FRAGMENT_TAG = "Explore"

    fun newInstance(): ExploreFragment {
      return ExploreFragment()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidSupportInjection.inject(this)
  }

  override fun onResume() {
    super.onResume()
    presenter.updateFragmentName()
  }

}