package zebrostudio.wallr100.ui.explore

import dagger.Module
import dagger.Provides

@Module
class ExploreFragmentModule {

  @Provides
  internal fun provideExplorePresenter() = ExplorePresenterImpl()
}