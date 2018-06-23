package zebrostudio.wallr100.ui.minimal

import dagger.Module
import dagger.Provides

@Module
class MinimalFragmentModule {

  @Provides
  internal fun provideMinimalPresenter() = MinimalPresenterImpl()

}