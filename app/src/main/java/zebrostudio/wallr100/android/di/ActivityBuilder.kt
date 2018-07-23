package zebrostudio.wallr100.android.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.main.MainActivityModule

@Module
abstract class ActivityBuilder {

  @ContributesAndroidInjector(modules = [(MainActivityModule::class), (FragmentProvider::class)])
  abstract fun mainActivity(): MainActivity

  @ContributesAndroidInjector()
  abstract fun buyProActivity(): BuyProActivity

}
