package zebrostudio.wallr100.ui.main

import dagger.Module
import dagger.Provides

@Suppress("NOTHING_TO_INLINE")
@Module
class MainActivityModule {

  @Provides
  internal inline fun provideMainPresenter(): MainContract.MainPresenter = MainActivityPresenterImpl()

}