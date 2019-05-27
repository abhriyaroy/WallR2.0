package zebrostudio.wallr100.android.ui.collection

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.permissions.PermissionsHelper
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
  fun provideCollectionPresenter(
    widgetHintsUseCase: WidgetHintsUseCase,
    userPremiumStatusUseCase: UserPremiumStatusUseCase,
    collectionImagesUseCase: CollectionImagesUseCase,
    collectionImagesPresenterEntityMapper: CollectionImagesPresenterEntityMapper,
    wallpaperSetter: WallpaperSetter,
    resourceUtils: ResourceUtils,
    postExecutionThread: PostExecutionThread,
    permissionsHelper: PermissionsHelper
  ): CollectionPresenter = CollectionPresenterImpl(widgetHintsUseCase,
      userPremiumStatusUseCase, collectionImagesUseCase, collectionImagesPresenterEntityMapper,
      wallpaperSetter, resourceUtils, postExecutionThread, permissionsHelper)

  @Provides
  fun provideCollectionPresenterEntityMapper()
      : CollectionImagesPresenterEntityMapper = CollectionsImagesPresenterEntityMapperImpl()

}