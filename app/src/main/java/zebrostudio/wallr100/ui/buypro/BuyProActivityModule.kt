package zebrostudio.wallr100.ui.buypro

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.data.DataRepository

@Module
class BuyProActivityModule {

  @Provides
  internal fun provideBuyProPresenter(dataRepository: DataRepository)
      : BuyProContract.BuyProPresenter = BuyProPresenterImpl(dataRepository)

}