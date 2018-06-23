package zebrostudio.wallr100.ui.explore

class ExplorePresenterImpl : ExploreContract.ExplorePresenter {

  private var exploreView: ExploreContract.ExploreView? = null

  override fun attachView(view: ExploreContract.ExploreView) {
    exploreView = view
  }

  override fun detachView() {
    exploreView = null
  }

}