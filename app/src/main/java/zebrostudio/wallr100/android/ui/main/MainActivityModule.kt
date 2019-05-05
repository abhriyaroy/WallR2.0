package zebrostudio.wallr100.android.ui.main

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.main.MainContract.MainPresenter
import zebrostudio.wallr100.presentation.main.MainPresenterImpl

@Module
class MainActivityModule {

  @Provides
  fun provideMainPresenter(
    widgetHintsUseCase: WidgetHintsUseCase,
    userPremiumStatusUseCase: UserPremiumStatusUseCase
  ): MainPresenter = MainPresenterImpl(widgetHintsUseCase, userPremiumStatusUseCase)

}