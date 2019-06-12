package zebrostudio.wallr100.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.android.di.scopes.PerActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProModule
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailActivity
import zebrostudio.wallr100.android.ui.detail.colors.ColorsDetailModule
import zebrostudio.wallr100.android.ui.detail.images.DetailActivity
import zebrostudio.wallr100.android.ui.detail.images.DetailActivityModule
import zebrostudio.wallr100.android.ui.expandimage.FullScreenImageActivity
import zebrostudio.wallr100.android.ui.expandimage.FullScreenImageModule
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.ui.main.MainActivityModule
import zebrostudio.wallr100.android.ui.search.SearchActivity
import zebrostudio.wallr100.android.ui.search.SearchActivityModule

@Module
abstract class ActivityBuilder {

  @PerActivity
  @ContributesAndroidInjector(modules = [(MainActivityModule::class), (FragmentProvider::class)])
  abstract fun mainActivityInjector(): MainActivity

  @PerActivity
  @ContributesAndroidInjector(modules = [(BuyProModule::class)])
  abstract fun buyProActivityInjector(): BuyProActivity

  @PerActivity
  @ContributesAndroidInjector(modules = [(SearchActivityModule::class)])
  abstract fun searchActivityInjector(): SearchActivity

  @PerActivity
  @ContributesAndroidInjector(modules = [(DetailActivityModule::class)])
  abstract fun detailActivityInjector(): DetailActivity

  @PerActivity
  @ContributesAndroidInjector(modules = [ColorsDetailModule::class])
  abstract fun colorsDetailActivityInjector(): ColorsDetailActivity

  @PerActivity
  @ContributesAndroidInjector(modules = [(FullScreenImageModule::class)])
  abstract fun fullScreenImageActivityInjector(): FullScreenImageActivity

}
