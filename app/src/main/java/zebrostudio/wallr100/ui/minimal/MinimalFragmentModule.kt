package zebrostudio.wallr100.ui.minimal

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.data.DataRepository

@Module
class MinimalFragmentModule {

  @Provides
  internal fun provideMinimalPresenter(dataRepository: DataRepository)
      : MinimalContract.MinimalPresenter = MinimalPresenterImpl(dataRepository)

}