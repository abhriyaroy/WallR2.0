package zebrostudio.wallr100.di

import dagger.Module
import zebrostudio.wallr100.ui.main.MainActivity
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.ui.buypro.BuyProActivity
import zebrostudio.wallr100.ui.main.MainActivityModule

@Module
abstract class ActivityBuilder {

  @ContributesAndroidInjector(modules = [(MainActivityModule::class), (FragmentProvider::class)])
  abstract fun mainActivity(): MainActivity

  @ContributesAndroidInjector()
  abstract fun buyProActivity(): BuyProActivity

}
