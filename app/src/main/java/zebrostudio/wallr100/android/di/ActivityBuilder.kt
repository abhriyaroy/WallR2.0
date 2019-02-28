package zebrostudio.wallr100.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.android.di.scopes.PerActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProModule
import zebrostudio.wallr100.android.ui.detail.DetailActivity
import zebrostudio.wallr100.android.ui.detail.DetailActivityModule
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.ui.main.MainActivityModule
import zebrostudio.wallr100.android.ui.search.SearchActivity
import zebrostudio.wallr100.android.ui.search.SearchActivityModule

@Module
abstract class ActivityBuilder {

  @PerActivity
  @ContributesAndroidInjector(modules = [(MainActivityModule::class), (FragmentProvider::class)])
  abstract fun mainActivity(): MainActivity

  @PerActivity
  @ContributesAndroidInjector(modules = [(BuyProModule::class)])
  abstract fun buyProActivity(): BuyProActivity

  @PerActivity
  @ContributesAndroidInjector(modules = [(SearchActivityModule::class)])
  abstract fun searchActivity(): SearchActivity

  @PerActivity
  @ContributesAndroidInjector(modules = [(DetailActivityModule::class)])
  abstract fun detailActivity(): DetailActivity

}
