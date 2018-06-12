package zebrostudio.wallr100.di

import dagger.Module
import zebrostudio.wallr100.ui.main.MainActivity
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

  @ContributesAndroidInjector()
  abstract fun mainActivity(): MainActivity

}