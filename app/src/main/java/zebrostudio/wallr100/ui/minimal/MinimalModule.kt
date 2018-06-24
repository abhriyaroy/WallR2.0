package zebrostudio.wallr100.ui.minimal

import dagger.Module
import dagger.Provides

@Module
class MinimalModule {

  @Provides
  internal fun provideMinimalPresenter(): MinimalContract.MinimalPresenter = MinimalPresenterImpl()

}