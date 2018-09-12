package zebrostudio.wallr100.presentation.search

import zebrostudio.wallr100.android.BasePresenter
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity

interface SearchContract {

  interface SearchView {
    fun showLoader()
    fun hideLoader()
    fun showNoInputView()
    fun showNoResultView(query: String?)
    fun hideAll()
    fun showNoInternetView()
    fun showGenericErrorMessage()
    fun showSearchResults(list: List<SearchPicturesPresenterEntity>)
    fun appendSearchResults(list: List<SearchPicturesPresenterEntity>)
  }

  interface SearchPresenter : BasePresenter<SearchView> {
    fun notifyQuerySubmitted(query: String?)
    fun fetchMoreImages(query: String?)
  }

}