package zebrostudio.wallr100.android.ui.wallpaper

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.presentation.wallpaper.explore.ImageListContract
import zebrostudio.wallr100.presentation.wallpaper.explore.ImageListPresenterImpl
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper

@Module
class ImageListModule {

  @Provides
  fun provideImagePresenterEntityMapper(): ImagePresenterEntityMapper = ImagePresenterEntityMapper()

  @Provides fun provideExplorePresenter(
    wallpaperImagesUseCase: WallpaperImagesUseCase,
    imagePresenterEntityMapper: ImagePresenterEntityMapper
  ): ImageListContract.ImageListPresenter = ImageListPresenterImpl(wallpaperImagesUseCase,
      imagePresenterEntityMapper)
}