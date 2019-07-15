package zebrostudio.wallr100.android.ui.search

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.di.scopes.PerActivity
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.presentation.search.SearchContract.SearchPresenter
import zebrostudio.wallr100.presentation.search.SearchPresenterImpl
import zebrostudio.wallr100.presentation.search.mapper.SearchPicturesPresenterEntityMapper

@Module
class SearchActivityModule {

  @Provides
  @PerActivity
  fun providesSearchPicturePresentationEntityMapper()
      : SearchPicturesPresenterEntityMapper = SearchPicturesPresenterEntityMapper()

  @Provides
  @PerActivity
  fun providesSearchPresenterImpl(
    searchPicturesUseCase: SearchPicturesUseCase,
    searchPicturesPresenterEntityMapper: SearchPicturesPresenterEntityMapper,
    postExecutionThread: PostExecutionThread
  ): SearchPresenter = SearchPresenterImpl(searchPicturesUseCase,
    searchPicturesPresenterEntityMapper, postExecutionThread)

}