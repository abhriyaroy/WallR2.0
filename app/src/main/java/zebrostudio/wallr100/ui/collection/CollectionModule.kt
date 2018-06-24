package zebrostudio.wallr100.ui.collection

import dagger.Module
import dagger.Provides

@Suppress("NOTHING_TO_INLINE")
@Module
class CollectionModule {

  @Provides
  internal inline fun provideCollectionPresenter(): CollectionContract.CollectionPresenter = CollectionPresenterImpl()

}