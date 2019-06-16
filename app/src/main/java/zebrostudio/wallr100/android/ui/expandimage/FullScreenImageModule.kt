package zebrostudio.wallr100.android.ui.expandimage

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.di.scopes.PerActivity
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImagePresenter
import zebrostudio.wallr100.presentation.expandimage.FullScreenImagePresenterImpl

@Module
class FullScreenImageModule {

  @Provides
  @PerActivity
  fun providesFullScreenImagePresenter(
    imageOptionsUseCase: ImageOptionsUseCase,
    postExecutionThread: PostExecutionThread
  ): FullScreenImagePresenter = FullScreenImagePresenterImpl(imageOptionsUseCase,
      postExecutionThread)

}