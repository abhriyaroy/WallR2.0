package zebrostudio.wallr100.presentation.search

import android.content.Intent
import zebrostudio.wallr100.presentation.BasePresenter
import zebrostudio.wallr100.presentation.BaseView
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity

interface SearchContract {

  interface SearchView : BaseView {
    fun showLoader()
    fun hideLoader()
    fun showBottomLoader()
    fun hideBottomLoader()
    fun showNoInputView()
    fun showNoResultView(query: String)
    fun hideAllLoadersAndMessageViews()
    fun showNoInternetView()
    fun showNoInternetToast()
    fun showGenericErrorView()
    fun showGenericErrorToast()
    fun showInputASearchQueryMessageView()
    fun showSearchResults(list: List<SearchPicturesPresenterEntity>)
    fun appendSearchResults(startPosition: Int, list: List<SearchPicturesPresenterEntity>)
    fun setEndlessLoadingToFalse()
    fun setSearchQueryWithoutSubmitting(searchWord: String)
  }

  interface SearchPresenter : BasePresenter<SearchView> {
    fun notifyQuerySubmitted(query: String)
    fun fetchMoreImages()
    fun notifyRetryButtonClicked()
    fun notifyActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
  }

}