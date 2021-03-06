package zebrostudio.wallr100.android.ui.collection

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.di.scopes.PerFragment
import zebrostudio.wallr100.android.permissions.PermissionsChecker
import zebrostudio.wallr100.android.system.SystemInfoProvider
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionPresenter
import zebrostudio.wallr100.presentation.collection.CollectionPresenterImpl
import zebrostudio.wallr100.presentation.collection.mapper.CollectionImagesPresenterEntityMapper
import zebrostudio.wallr100.presentation.collection.mapper.CollectionsImagesPresenterEntityMapperImpl

@Module
class CollectionModule {

  @Provides
  @PerFragment
  fun provideCollectionPresenter(
    widgetHintsUseCase: WidgetHintsUseCase,
    userPremiumStatusUseCase: UserPremiumStatusUseCase,
    collectionImagesUseCase: CollectionImagesUseCase,
    collectionImagesPresenterEntityMapper: CollectionImagesPresenterEntityMapper,
    wallpaperSetter: WallpaperSetter,
    resourceUtils: ResourceUtils,
    postExecutionThread: PostExecutionThread,
    permissionsChecker: PermissionsChecker,
    systemInfoProvider: SystemInfoProvider
  ): CollectionPresenter = CollectionPresenterImpl(widgetHintsUseCase,
    userPremiumStatusUseCase, collectionImagesUseCase, collectionImagesPresenterEntityMapper,
    wallpaperSetter, resourceUtils, postExecutionThread, permissionsChecker,
    systemInfoProvider)

  @Provides
  @PerFragment
  fun provideCollectionPresenterEntityMapper()
      : CollectionImagesPresenterEntityMapper = CollectionsImagesPresenterEntityMapperImpl()

}