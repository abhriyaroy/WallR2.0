package zebrostudio.wallr100.ui.collection

import dagger.Module
import dagger.Provides

@Module
class CollectionModule {

  @Provides
  internal fun provideCollectionPresenter(): CollectionContract.CollectionPresenter = CollectionPresenterImpl()

}