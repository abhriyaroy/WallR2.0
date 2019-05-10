package zebrostudio.wallr100.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService

@Module
abstract class ServiceBuilder {

  @ContributesAndroidInjector()
  abstract fun automaticWallpaperChangerService(): AutomaticWallpaperChangerService
}