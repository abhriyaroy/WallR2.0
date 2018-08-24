package zebrostudio.wallr100.presentation.search

import zebrostudio.wallr100.android.BasePresenter

interface SearchContract {

  interface SearchView {
    fun showLoader()
    fun hideLoader()
    fun showNoInputView()
    fun showNoResultView(query: String?)
    fun hideAll()
    fun showNoInternetView()
    fun showGenericErrorMessage()
  }

  interface SearchPresenter : BasePresenter<SearchView> {
    fun notifyQuerySubmitted(query: String?)
  }

}