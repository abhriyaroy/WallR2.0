package zebrostudio.wallr100.android.ui.collection

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.presentation.collection.CollectionContract
import zebrostudio.wallr100.presentation.collection.CollectionPresenterImpl

@Module
class CollectionModule {

  @Provides
  fun provideCollectionPresenter():
      CollectionContract.CollectionPresenter = CollectionPresenterImpl()

}