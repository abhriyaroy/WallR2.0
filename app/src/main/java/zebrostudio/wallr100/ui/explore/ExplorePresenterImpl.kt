package zebrostudio.wallr100.ui.explore

import zebrostudio.wallr100.data.DataRepository
import javax.inject.Inject

class ExplorePresenterImpl
@Inject constructor(private var dataRepository: DataRepository) : ExploreContract.ExplorePresenter {

  private var exploreView: ExploreContract.ExploreView? = null

  override fun attachView(view: ExploreContract.ExploreView) {
    exploreView = view
  }

  override fun detachView() {
    exploreView = null
  }

  override fun updateFragmentName(name: String) {
    dataRepository.updateCurrentFragmentName(name)
  }

}