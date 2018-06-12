package zebrostudio.wallr100.di

import dagger.Module
import zebrostudio.wallr100.ui.main.MainActivity
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.ui.main.MainActivityModule

@Module
abstract class ActivityBuilder {

  @ContributesAndroidInjector(modules = [(MainActivityModule::class)])
  abstract fun mainActivity(): MainActivity

}
