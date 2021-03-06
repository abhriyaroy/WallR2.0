package zebrostudio.wallr100.android.ui.detail.colors

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.di.scopes.PerActivity
import zebrostudio.wallr100.android.permissions.PermissionsChecker
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ColorImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailContract.ColorsDetailPresenter
import zebrostudio.wallr100.presentation.detail.colors.ColorsDetailPresenterImpl

@Module
class ColorsDetailModule {

  @Provides
  @PerActivity
  fun providesColorsDetailPresenter(
    resourceUtils: ResourceUtils,
    postExecutionThread: PostExecutionThread,
    isUserPremiumStatusUseCase: UserPremiumStatusUseCase,
    colorImagesUseCase: ColorImagesUseCase,
    wallpaperSetter: WallpaperSetter,
    permissionsChecker: PermissionsChecker
  ): ColorsDetailPresenter = ColorsDetailPresenterImpl(
    resourceUtils,
    postExecutionThread,
    isUserPremiumStatusUseCase,
    colorImagesUseCase,
    wallpaperSetter,
    permissionsChecker)
}