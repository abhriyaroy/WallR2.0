package zebrostudio.wallr100.android.ui.expandimage

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImagePresenter
import zebrostudio.wallr100.presentation.expandimage.FullScreenImagePresenterImpl

@Module
class FullScreenImageModule {

  @Provides
  fun providesFullScreenImagePresenter(): FullScreenImagePresenter = FullScreenImagePresenterImpl()

}