package zebrostudio.wallr100.ui.wallpaper

import dagger.Module
import dagger.Provides

@Suppress("NOTHING_TO_INLINE")
@Module
class WallpaperModule {

  @Provides
  internal inline fun provideWallpaperPresenter(): WallpaperContract.WallpaperPresenter = WallpaperPresenterImpl()

}