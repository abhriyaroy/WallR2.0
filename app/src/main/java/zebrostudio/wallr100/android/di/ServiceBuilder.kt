package zebrostudio.wallr100.android.di

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerServiceImpl

@Module
abstract class ServiceBuilder {

  @ContributesAndroidInjector
  abstract fun automaticWallpaperChangerService(): AutomaticWallpaperChangerServiceImpl

}