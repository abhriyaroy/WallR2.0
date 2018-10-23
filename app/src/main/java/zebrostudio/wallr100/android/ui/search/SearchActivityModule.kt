package zebrostudio.wallr100.android.ui.search

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.presentation.search.SearchContract
import zebrostudio.wallr100.presentation.search.mapper.SearchPicturesPresenterEntityMapper
import zebrostudio.wallr100.presentation.search.SearchPresenterImpl

@Module
class SearchActivityModule {

  @Provides
  fun providesSearchPicturePresentationEntityMapper()
      : SearchPicturesPresenterEntityMapper = SearchPicturesPresenterEntityMapper()

  @Provides
  fun providesSearchPresenterImpl(
    searchPicturesUseCase: SearchPicturesUseCase,
    searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper
  ): SearchContract.SearchPresenter = SearchPresenterImpl(searchPicturesUseCase,
      searchPicturesPresenterEntityMapper)

}