package zebrostudio.wallr100.ui.explore

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.data.DataRepository

@Module
class ExploreFragmentModule {

  @Provides
  internal fun provideExplorePresenter(dataRepository: DataRepository)
      : ExploreContract.ExplorePresenter = ExplorePresenterImpl(dataRepository)
}