package zebrostudio.wallr100.ui.explore

import zebrostudio.wallr100.data.DataRepository

class ExplorePresenterImpl
constructor(dataRepository: DataRepository) : ExploreContract.ExplorePresenter {

  private val EXPLORE_FRAGMENT_TAG = "Explore"
  private var dataRepository = dataRepository
  private var exploreView: ExploreContract.ExploreView? = null

  override fun attachView(view: ExploreContract.ExploreView) {
    exploreView = view
  }

  override fun detachView() {
    exploreView = null
  }

  override fun updateFragmentName() {
    dataRepository.updateCurrentFragmentName(EXPLORE_FRAGMENT_TAG)
  }

}