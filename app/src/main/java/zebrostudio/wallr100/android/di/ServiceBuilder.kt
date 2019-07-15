package zebrostudio.wallr100.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.android.di.scopes.PerService
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerServiceImpl

@Module
abstract class ServiceBuilder {

  @PerService
  @ContributesAndroidInjector
  abstract fun automaticWallpaperChangerService(): AutomaticWallpaperChangerServiceImpl

}
