package zebrostudio.wallr100.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import zebrostudio.wallr100.ui.collection.CollectionFragment
import zebrostudio.wallr100.ui.collection.CollectionFragmentModule
import zebrostudio.wallr100.ui.explore.ExploreFragment
import zebrostudio.wallr100.ui.explore.ExploreFragmentModule
import zebrostudio.wallr100.ui.minimal.MinimalFragment
import zebrostudio.wallr100.ui.minimal.MinimalFragmentModule

@Module
abstract class FragmentProvider {

  @ContributesAndroidInjector(modules = [(ExploreFragmentModule::class)])
  abstract fun exploreFragment(): ExploreFragment

  @ContributesAndroidInjector(modules = [(MinimalFragmentModule::class)])
  abstract fun minimalFragment(): MinimalFragment

  @ContributesAndroidInjector(modules = [(CollectionFragmentModule::class)])
  abstract fun collectionFragment(): CollectionFragment

}