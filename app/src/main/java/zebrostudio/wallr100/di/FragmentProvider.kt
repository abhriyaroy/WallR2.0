package zebrostudio.wallr100.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.ui.categories.CategoriesFragment
import zebrostudio.wallr100.ui.categories.CategoriesFragmentModule
import zebrostudio.wallr100.ui.explore.ExploreFragment
import zebrostudio.wallr100.ui.explore.ExploreFragmentModule
import zebrostudio.wallr100.ui.toppicks.ToppicksFragment
import zebrostudio.wallr100.ui.toppicks.ToppicksFragmentModule

@Module
abstract class FragmentProvider {

  @ContributesAndroidInjector(modules = [(ExploreFragmentModule::class)])
  abstract fun exploreFragment(): ExploreFragment

  @ContributesAndroidInjector(modules = [(ToppicksFragmentModule::class)])
  abstract fun toppicksFragment() : ToppicksFragment

  @ContributesAndroidInjector(modules = [(CategoriesFragmentModule::class)])
  abstract fun categoriesFragment() : CategoriesFragment

}