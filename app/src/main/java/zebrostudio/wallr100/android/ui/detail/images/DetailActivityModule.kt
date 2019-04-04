package zebrostudio.wallr100.android.ui.detail.images

import android.content.Context
import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.detail.images.DetailContract
import zebrostudio.wallr100.presentation.detail.images.DetailPresenterImpl
import zebrostudio.wallr100.presentation.detail.images.mapper.ImageDownloadPresenterEntityMapper

@Module
class DetailActivityModule {

  @Provides
  fun providesImageDownloadPresenterEntityMapper(): ImageDownloadPresenterEntityMapper =
      ImageDownloadPresenterEntityMapper()

  @Provides
  fun providesDetailPresenter(
    context: Context,
    imageOptionsUseCase: ImageOptionsUseCase,
    userPremiumStatusUseCase: UserPremiumStatusUseCase,
    wallpaperSetter: WallpaperSetter,
    postExecutionThread: PostExecutionThread,
    imageDownloadPresenterEntityMapper: ImageDownloadPresenterEntityMapper
  ): DetailContract.DetailPresenter = DetailPresenterImpl(
      context,
      imageOptionsUseCase,
      userPremiumStatusUseCase,
      wallpaperSetter,
      postExecutionThread,
      imageDownloadPresenterEntityMapper)
}
