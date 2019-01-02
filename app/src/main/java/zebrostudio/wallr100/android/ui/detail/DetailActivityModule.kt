package zebrostudio.wallr100.android.ui.detail

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.presentation.detail.DetailContract
import zebrostudio.wallr100.presentation.detail.DetailPresenterImpl

@Module
class DetailActivityModule {

  @Provides
  fun providesDetailPresenter(): DetailContract.DetailPresenter = DetailPresenterImpl()
}
