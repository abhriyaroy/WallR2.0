package zebrostudio.wallr100.android.ui.buypro

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.presentation.buypro.BuyProContract
import zebrostudio.wallr100.presentation.buypro.BuyProPresenterImpl

@Module
class BuyProModule {

  @Provides
  internal fun provideBuyProPresenter(): BuyProContract.BuyProPresenter = BuyProPresenterImpl()

}