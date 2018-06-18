package zebrostudio.wallr100.ui.explore

import zebrostudio.wallr100.data.DataRepository

class ExplorePresenterImpl
constructor(dataRepository: DataRepository) : ExploreContract.ExplorePresenter {

  private var exploreView: ExploreContract.ExploreView? = null

  override fun attachView(view: ExploreContract.ExploreView) {
    exploreView = view
  }

  override fun detachView() {
    exploreView = null
  }

  override fun updateFragmentName() {

  }

}