package zebrostudio.wallr100.ui.collection

import dagger.Module
import dagger.Provides

@Module
class CollectionFragmentModule {

  @Provides
  internal fun provideCollectionPresenter() = CollectionPresenterImpl()

}