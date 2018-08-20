package zebrostudio.wallr100.presentation.search

import zebrostudio.wallr100.android.BasePresenter

interface SearchContract{

  interface SearchView{
    fun showLoader()
    fun hideLoader()
    fun showNoInputView()
    fun hideNoInputView()
    fun showNoResultView()
    fun hideNoResultView()
  }

  interface SearchPresenter : BasePresenter<SearchView>{
    fun notifyQuerySubmitted(query: String?)
  }

}