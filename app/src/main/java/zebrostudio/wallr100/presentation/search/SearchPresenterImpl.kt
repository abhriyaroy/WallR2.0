package zebrostudio.wallr100.presentation.search

class SearchPresenterImpl : SearchContract.SearchPresenter {

  private var searchView : SearchContract.SearchView? = null

  override fun attachView(view: SearchContract.SearchView) {
    searchView = view
  }

  override fun detachView() {
    searchView = null
  }

  override fun notifyQuerySubmitted(query: String?) {

  }

}