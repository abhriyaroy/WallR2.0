package zebrostudio.wallr100.android.ui.detail.colors

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ColorsDetailsUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailPresenter
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailPresenterImpl

@Module
class ColorsDetailModule {

  @Provides
  fun providesColorsDetailPresenter(
    postExecutionThread: PostExecutionThread,
    isUserPremiumStatusUseCase: UserPremiumStatusUseCase,
    colorsDetailsUseCase: ColorsDetailsUseCase
  ): ColorsDetailPresenter = ColorsDetailPresenterImpl(
      postExecutionThread,
      isUserPremiumStatusUseCase,
      colorsDetailsUseCase)
}