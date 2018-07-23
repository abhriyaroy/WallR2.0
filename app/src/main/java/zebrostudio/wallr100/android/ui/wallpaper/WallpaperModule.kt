package zebrostudio.wallr100.android.ui.wallpaper

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.presentation.wallpaper.WallpaperContract
import zebrostudio.wallr100.presentation.wallpaper.WallpaperPresenterImpl

@Module
class WallpaperModule {

  @Provides
  internal fun provideWallpaperPresenter():
      WallpaperContract.WallpaperPresenter = WallpaperPresenterImpl()

}