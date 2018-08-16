package zebrostudio.wallr100.presentation.search

import zebrostudio.wallr100.android.BasePresenter

interface SearchContract{

  interface SearchView

  interface SearchPresenter : BasePresenter<SearchView>

}