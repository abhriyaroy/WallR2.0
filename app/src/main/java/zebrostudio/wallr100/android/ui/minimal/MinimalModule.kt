package zebrostudio.wallr100.android.ui.minimal

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.MinimalImagesUseCase
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalPresenterImpl

@Module
class MinimalModule {

  @Provides
  internal fun provideMinimalPresenter(
    minimalImagesUseCase: MinimalImagesUseCase,
    postExecutionThread: PostExecutionThread
  ): MinimalPresenter = MinimalPresenterImpl(minimalImagesUseCase,
      postExecutionThread)

}