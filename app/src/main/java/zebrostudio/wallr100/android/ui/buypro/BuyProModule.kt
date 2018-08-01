package zebrostudio.wallr100.android.ui.buypro

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.presentation.buypro.BuyProContract
import zebrostudio.wallr100.presentation.buypro.BuyProPresenterImpl
import zebrostudio.wallr100.presentation.mapper.ProAuthPresentationMapperImpl

@Module
class BuyProModule {

  @Provides
  internal fun provideBuyProPresenter(
    authenticatePurchaseUseCase: AuthenticatePurchaseUseCase,
    proAuthPresentationMapperImpl: ProAuthPresentationMapperImpl
  ): BuyProContract.BuyProPresenter = BuyProPresenterImpl(authenticatePurchaseUseCase,
      proAuthPresentationMapperImpl)

}