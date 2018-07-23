package zebrostudio.wallr100.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.android.ui.collection.CollectionFragment
import zebrostudio.wallr100.android.ui.collection.CollectionModule
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperFragment
import zebrostudio.wallr100.android.ui.minimal.MinimalFragment
import zebrostudio.wallr100.android.ui.minimal.MinimalModule
import zebrostudio.wallr100.android.ui.wallpaper.WallpaperModule

@Module
abstract class FragmentProvider {

  @ContributesAndroidInjector(modules = [(WallpaperModule::class)])
  abstract fun wallpaperFragment(): WallpaperFragment

  @ContributesAndroidInjector(modules = [(MinimalModule::class)])
  abstract fun minimalFragment(): MinimalFragment

  @ContributesAndroidInjector(modules = [(CollectionModule::class)])
  abstract fun collectionFragment(): CollectionFragment

}