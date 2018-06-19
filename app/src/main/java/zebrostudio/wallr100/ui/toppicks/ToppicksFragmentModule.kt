package zebrostudio.wallr100.ui.toppicks

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.data.DataRepository

@Module
class ToppicksFragmentModule {

  @Provides
  internal fun provideToppicksPresenter(dataRepository: DataRepository)
      : ToppicksContract.ToppicksPresenter = ToppicksPresenterImpl(dataRepository)
}