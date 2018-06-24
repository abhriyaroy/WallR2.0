package zebrostudio.wallr100.ui.minimal

import dagger.Module
import dagger.Provides

@Suppress("NOTHING_TO_INLINE")
@Module
class MinimalModule {

  @Provides
  internal inline fun provideMinimalPresenter(): MinimalContract.MinimalPresenter = MinimalPresenterImpl()

}