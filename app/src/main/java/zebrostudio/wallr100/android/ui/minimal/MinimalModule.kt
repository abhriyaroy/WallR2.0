package zebrostudio.wallr100.android.ui.minimal

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.di.scopes.PerFragment
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.MinimalImagesUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalPresenterImpl

@Module
class MinimalModule {

  @Provides
  @PerFragment
  internal fun provideMinimalPresenter(
    widgetHintsUseCase: WidgetHintsUseCase,
    minimalImagesUseCase: MinimalImagesUseCase,
    postExecutionThread: PostExecutionThread
  ): MinimalPresenter = MinimalPresenterImpl(
    widgetHintsUseCase,
    minimalImagesUseCase,
    postExecutionThread)

}