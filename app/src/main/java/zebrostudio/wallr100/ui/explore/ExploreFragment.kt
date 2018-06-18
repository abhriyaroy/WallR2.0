package zebrostudio.wallr100.ui.explore

import zebrostudio.wallr100.R
import zebrostudio.wallr100.ui.basefragment.BaseFragment
import zebrostudio.wallr100.utils.stringRes
import javax.inject.Inject

class ExploreFragment : BaseFragment(){

  @Inject
  internal lateinit var presenter: ExplorePresenterImpl

  val FRAGMENTNAME = activity?.stringRes(R.string.guillotine_explore_title)

  override fun onResume() {
    super.onResume()
    presenter.updateFragmentName()
  }
}