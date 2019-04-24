package zebrostudio.wallr100.android.ui.wallpaper

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.presentation.wallpaper.ImageListContract.ImageListPresenter
import zebrostudio.wallr100.presentation.wallpaper.ImageListPresenterImpl
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper

@Module
class ImageListModule {

  @Provides
  fun provideImagePresenterEntityMapper(): ImagePresenterEntityMapper = ImagePresenterEntityMapper()

  @Provides fun provideImageListPresenter(
    wallpaperImagesUseCase: WallpaperImagesUseCase,
    imagePresenterEntityMapper: ImagePresenterEntityMapper,
    postExecutionThread: PostExecutionThread
  ): ImageListPresenter = ImageListPresenterImpl(
      wallpaperImagesUseCase,
      imagePresenterEntityMapper,
      postExecutionThread)
}