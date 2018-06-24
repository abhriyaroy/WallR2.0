package zebrostudio.wallr100.ui.wallpaper

import dagger.Module
import dagger.Provides

@Module
class WallpaperModule {

  @Provides
  internal fun provideWallpaperPresenter(): WallpaperContract.WallpaperPresenter = WallpaperPresenterImpl()

}