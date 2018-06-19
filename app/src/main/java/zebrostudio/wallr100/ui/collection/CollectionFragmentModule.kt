package zebrostudio.wallr100.ui.collection

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.data.DataRepository

@Module
class CollectionFragmentModule{

  @Provides
  internal fun provideCollectionPresenter(dataRepository: DataRepository)
      : CollectionContract.CollectionPresenter = CollectionPresenterImpl(dataRepository)

}