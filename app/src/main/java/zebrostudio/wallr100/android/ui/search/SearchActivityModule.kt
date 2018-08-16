package zebrostudio.wallr100.android.ui.search

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.presentation.search.SearchContract
import zebrostudio.wallr100.presentation.search.SearchPresenterImpl

@Module
class SearchActivityModule {

  @Provides
  internal fun providesSearchPresenterImpl(): SearchContract.SearchPresenter = SearchPresenterImpl()

}