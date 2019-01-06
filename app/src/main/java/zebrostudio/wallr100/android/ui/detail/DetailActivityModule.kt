package zebrostudio.wallr100.android.ui.detail

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.interactor.ShareImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.detail.DetailContract
import zebrostudio.wallr100.presentation.detail.DetailPresenterImpl

@Module
class DetailActivityModule {

  @Provides
  fun providesDetailPresenter(
    shareImagesUseCase: ShareImagesUseCase,
    userPremiumStatusUseCase: UserPremiumStatusUseCase
  ): DetailContract.DetailPresenter = DetailPresenterImpl(shareImagesUseCase,
      userPremiumStatusUseCase)
}
