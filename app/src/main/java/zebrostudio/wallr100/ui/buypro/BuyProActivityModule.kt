package zebrostudio.wallr100.ui.buypro

import dagger.Module
import dagger.Provides

@Module
class BuyProActivityModule {

  @Provides
  internal fun provideBuyProPresenter(): BuyProContract.BuyProPresenter = BuyProPresenterImpl()

}