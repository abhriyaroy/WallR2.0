package zebrostudio.wallr100.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.ui.explore.ExploreFragment

@Module
abstract class FragmentProvider{

  @ContributesAndroidInjector
  abstract fun exploreFragment() : ExploreFragment

}