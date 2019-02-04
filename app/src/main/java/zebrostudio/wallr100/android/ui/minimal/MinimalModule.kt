package zebrostudio.wallr100.android.ui.minimal

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.presentation.minimal.MinimalContract
import zebrostudio.wallr100.presentation.minimal.MinimalPresenterImpl

@Module
class
MinimalModule {

  @Provides
  internal fun provideMinimalPresenter(): MinimalContract.MinimalPresenter = MinimalPresenterImpl()

}