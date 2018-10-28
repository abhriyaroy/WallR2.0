package zebrostudio.wallr100.android.ui.wallpaper

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.presentation.wallpaper.WallpaperContract
import zebrostudio.wallr100.presentation.wallpaper.WallpaperPresenterImpl
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper

@Module
class WallpaperModule {

  @Provides
  fun provideImagePresenterEntityMapper(): ImagePresenterEntityMapper = ImagePresenterEntityMapper()

  @Provides
  fun provideWallpaperPresenter(
    wallpaperImagesUseCase: WallpaperImagesUseCase,
    imagePresenterEntityMapper: ImagePresenterEntityMapper
  ):
      WallpaperContract.WallpaperPresenter = WallpaperPresenterImpl(wallpaperImagesUseCase,
      imagePresenterEntityMapper)

}