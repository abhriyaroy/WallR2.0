package zebrostudio.wallr100.presentation.search

import com.uber.autodispose.ScopeProvider
import zebrostudio.wallr100.android.BasePresenter
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity

interface SearchContract {

  interface SearchView {
    fun showLoader()
    fun hideLoader()
    fun showBottomLoader()
    fun hideBottomLoader()
    fun showNoInputView()
    fun showNoResultView(query: String?)
    fun hideAllLoadersAndMessageViews()
    fun showNoInternetView()
    fun showNoInternetToast()
    fun showGenericErrorView()
    fun showGenericErrorToast()
    fun showSearchResults(list: List<SearchPicturesPresenterEntity>)
    fun appendSearchResults(startPosition: Int, list: List<SearchPicturesPresenterEntity>)
    fun getScope(): ScopeProvider
    fun setEndlessLoadingToFalse()
  }

  interface SearchPresenter : BasePresenter<SearchView> {
    fun notifyQuerySubmitted(query: String?)
    fun fetchMoreImages()
  }

}