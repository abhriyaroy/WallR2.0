package zebrostudio.wallr100.android.ui.main

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.presentation.main.MainActivityPresenterImpl
import zebrostudio.wallr100.presentation.main.MainContract

@Module
class MainActivityModule {

  @Provides
  internal fun provideMainPresenter(): MainContract.MainPresenter = MainActivityPresenterImpl()

}