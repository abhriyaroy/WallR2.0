package zebrostudio.wallr100.android.ui.wallpaper.explore

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.presentation.wallpaper.explore.ExploreContract
import zebrostudio.wallr100.presentation.wallpaper.explore.ExplorePresenterImpl
import zebrostudio.wallr100.presentation.wallpaper.mapper.ImagePresenterEntityMapper

@Module
class ExploreModule {

  @Provides
  fun provideImagePresenterEntityMapper(): ImagePresenterEntityMapper = ImagePresenterEntityMapper()

  @Provides fun provideExplorePresenter(
    wallpaperImagesUseCase: WallpaperImagesUseCase,
    imagePresenterEntityMapper: ImagePresenterEntityMapper
  ): ExploreContract.ExplorePresenter = ExplorePresenterImpl(wallpaperImagesUseCase,
      imagePresenterEntityMapper)
}