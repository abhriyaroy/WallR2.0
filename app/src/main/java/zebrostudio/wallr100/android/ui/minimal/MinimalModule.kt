package zebrostudio.wallr100.android.ui.minimal

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.presentation.minimal.MinimalContract
import zebrostudio.wallr100.presentation.minimal.MinimalPresenterImpl

@Module
class MinimalModule {

  @Provides
  internal fun provideMinimalPresenter(
    wallrRepository: WallrRepository,
    postExecutionThread: PostExecutionThread
  ): MinimalContract.MinimalPresenter = MinimalPresenterImpl(wallrRepository, postExecutionThread)

}