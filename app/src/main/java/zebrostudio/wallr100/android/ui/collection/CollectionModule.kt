package zebrostudio.wallr100.android.ui.collection

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionPresenter
import zebrostudio.wallr100.presentation.collection.CollectionPresenterImpl

@Module
class CollectionModule {

  @Provides
  fun provideCollectionPresenter(
    widgetHintsUseCase: WidgetHintsUseCase,
    userPremiumStatusUseCase: UserPremiumStatusUseCase
  ): CollectionPresenter = CollectionPresenterImpl(widgetHintsUseCase, userPremiumStatusUseCase)

}