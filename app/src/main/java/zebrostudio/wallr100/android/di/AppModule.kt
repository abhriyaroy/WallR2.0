package zebrostudio.wallr100.android.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.AndroidBackgroundThreads
import zebrostudio.wallr100.android.AndroidMainThread
import zebrostudio.wallr100.android.notification.NotificationFactory
import zebrostudio.wallr100.android.notification.NotificationFactoryImpl
import zebrostudio.wallr100.android.permissions.PermissionsCheckerHelper
import zebrostudio.wallr100.android.permissions.PermissionsCheckerHelperImpl
import zebrostudio.wallr100.android.service.ServiceManager
import zebrostudio.wallr100.android.service.ServiceManagerImpl
import zebrostudio.wallr100.android.system.SystemInfoProvider
import zebrostudio.wallr100.android.system.SystemInfoProviderImpl
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcherImpl
import zebrostudio.wallr100.android.utils.GsonProvider
import zebrostudio.wallr100.android.utils.GsonProviderImpl
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.ResourceUtilsImpl
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.android.utils.WallpaperSetterImpl
import zebrostudio.wallr100.data.DownloadHelper
import zebrostudio.wallr100.data.DownloadHelperImpl
import zebrostudio.wallr100.data.FileHandler
import zebrostudio.wallr100.data.FileHandlerImpl
import zebrostudio.wallr100.data.FirebaseDatabaseHelper
import zebrostudio.wallr100.data.FirebaseDatabaseHelperImpl
import zebrostudio.wallr100.data.ImageHandler
import zebrostudio.wallr100.data.ImageHandlerImpl
import zebrostudio.wallr100.data.MinimalColorHelper
import zebrostudio.wallr100.data.MinimalColorsHelperImpl
import zebrostudio.wallr100.data.SharedPrefsHelper
import zebrostudio.wallr100.data.SharedPrefsHelperImpl
import zebrostudio.wallr100.data.WallrDataRepository
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactoryImpl
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UnsplashClientFactoryImpl
import zebrostudio.wallr100.data.database.DatabaseHelper
import zebrostudio.wallr100.data.database.DatabaseHelperImpl
import zebrostudio.wallr100.data.mapper.CollectionsDatabaseImageEntityMapper
import zebrostudio.wallr100.data.mapper.CollectionsDatabaseImageEntityMapperImpl
import zebrostudio.wallr100.data.mapper.DatabaseImageTypeMapper
import zebrostudio.wallr100.data.mapper.DatabaseImageTypeMapperImpl
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapper
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapperImpl
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapper
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapperImpl
import zebrostudio.wallr100.data.urlshortener.UrlShortener
import zebrostudio.wallr100.data.urlshortener.UrlShortenerImpl
import zebrostudio.wallr100.domain.TimeManager
import zebrostudio.wallr100.domain.TimeManagerImpl
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.ExecutionThread
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseInteractor
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerInteractor
import zebrostudio.wallr100.domain.interactor.AutomaticWallpaperChangerUseCase
import zebrostudio.wallr100.domain.interactor.CollectionImagesUseCase
import zebrostudio.wallr100.domain.interactor.CollectionsImagesInteractor
import zebrostudio.wallr100.domain.interactor.ColorImagesInteractor
import zebrostudio.wallr100.domain.interactor.ColorImagesUseCase
import zebrostudio.wallr100.domain.interactor.ImageOptionsInteractor
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.interactor.MinimalImagesInteractor
import zebrostudio.wallr100.domain.interactor.MinimalImagesUseCase
import zebrostudio.wallr100.domain.interactor.SearchPicturesInteractor
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusInteractor
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WallpaperImagesInteractor
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.domain.interactor.WidgetHintsInteractor
import zebrostudio.wallr100.domain.interactor.WidgetHintsUseCase
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerPresenterImpl
import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerContract.DragSelectItemPresenter
import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerPresenterImpl
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract.ImageRecyclerViewPresenter
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl
import javax.inject.Singleton

@Module
class AppModule {

  @Provides
  @Singleton
  fun provideContext(application: Application): Context {
    return application
  }

  @Provides
  @Singleton
  fun provideSystemInfo(): SystemInfoProvider = SystemInfoProviderImpl()

  @Provides
  @Singleton
  fun provideResourceUtils(context: Context): ResourceUtils = ResourceUtilsImpl(context)

  @Provides
  @Singleton
  fun provideFragmentTag(resourceUtils: ResourceUtils): FragmentNameTagFetcher = FragmentNameTagFetcherImpl(
      resourceUtils)

  @Provides
  @Singleton
  fun provideSharedPrefsHelper(context: Context): SharedPrefsHelper = SharedPrefsHelperImpl(context)

  @Provides
  @Singleton
  fun providesDatabaseHelper(context: Context): DatabaseHelper =
      DatabaseHelperImpl(context)

  @Provides
  @Singleton
  fun provideFirebaseDatabaseHelper(context: Context): FirebaseDatabaseHelper =
      FirebaseDatabaseHelperImpl(context)

  @Provides
  @Singleton
  fun provideMinimalColorHelper(
    context: Context,
    sharedPrefsHelper: SharedPrefsHelper
  ): MinimalColorHelper = MinimalColorsHelperImpl(context, sharedPrefsHelper)

  @Provides
  @Singleton
  fun provideGson(): GsonProvider = GsonProviderImpl()

  @Provides
  @Singleton
  fun provideAndroidMainThread(): PostExecutionThread = AndroidMainThread()

  @Provides
  @Singleton
  fun provideAndroidBackgroundThread(): ExecutionThread = AndroidBackgroundThreads()

  @Provides
  @Singleton
  fun provideRemoteAuthServiceFactory(): RemoteAuthServiceFactory =
      RemoteAuthServiceFactoryImpl()

  @Provides
  @Singleton
  fun provideUnsplashClientFactory(): UnsplashClientFactory = UnsplashClientFactoryImpl()

  @Provides
  fun provideCollectionsDatabaseImageEntityMapper():
      CollectionsDatabaseImageEntityMapper = CollectionsDatabaseImageEntityMapperImpl()

  @Provides
  fun provideDataBaseImageTypeMapper(): DatabaseImageTypeMapper = DatabaseImageTypeMapperImpl()

  @Provides
  fun provideUnsplashPictureEntityMapper(): UnsplashPictureEntityMapper = UnsplashPictureEntityMapperImpl()

  @Provides
  fun provideFirebasePictureEntityMapper(): FirebasePictureEntityMapper = FirebasePictureEntityMapperImpl()

  @Provides
  @Singleton
  fun provideUrlShortener(): UrlShortener = UrlShortenerImpl()

  @Provides
  fun provideFileHandler(context: Context): FileHandler = FileHandlerImpl(context)

  @Provides
  fun provideWallpaperSetter(context: Context): WallpaperSetter = WallpaperSetterImpl(context)

  @Provides
  fun provideDownloadHelper(
    context: Context,
    fileHandler: FileHandler
  ): DownloadHelper = DownloadHelperImpl(context, fileHandler)

  @Provides
  fun provideImageHandler(
    context: Context,
    fileHandler: FileHandler,
    databaseHelper: DatabaseHelper,
    wallpaperSetter: WallpaperSetter
  ): ImageHandler = ImageHandlerImpl(context, fileHandler, databaseHelper, wallpaperSetter)

  @Provides
  @Singleton
  fun provideWallrRepository(
    retrofitFirebaseAuthFactory: RemoteAuthServiceFactory,
    unsplashClientFactory: UnsplashClientFactory,
    sharedPrefsHelper: SharedPrefsHelper,
    gsonProvider: GsonProvider,
    collectionsDatabaseImageEntityMapper: CollectionsDatabaseImageEntityMapper,
    databaseImageTypeMapper: DatabaseImageTypeMapper,
    unsplashPictureEntityMapper: UnsplashPictureEntityMapper,
    firebaseDatabaseHelper: FirebaseDatabaseHelper,
    firebasePictureEntityMapper: FirebasePictureEntityMapper,
    urlShortener: UrlShortener,
    imageHandler: ImageHandler,
    fileHandler: FileHandler,
    downloadHelper: DownloadHelper,
    minimalColorHelper: MinimalColorHelper,
    executionThread: ExecutionThread
  ): WallrRepository = WallrDataRepository(retrofitFirebaseAuthFactory,
      unsplashClientFactory,
      sharedPrefsHelper,
      gsonProvider,
      collectionsDatabaseImageEntityMapper,
      databaseImageTypeMapper,
      unsplashPictureEntityMapper,
      firebaseDatabaseHelper,
      firebasePictureEntityMapper,
      urlShortener,
      imageHandler,
      fileHandler,
      downloadHelper,
      minimalColorHelper,
      executionThread)

  @Provides
  fun providesTimeManager(): TimeManager = TimeManagerImpl()

  @Provides
  @Singleton
  fun provideAuthenticatePurchaseUseCase(
    wallrRepository: WallrRepository
  ): AuthenticatePurchaseUseCase = AuthenticatePurchaseInteractor(wallrRepository)

  @Provides
  @Singleton
  fun provideUserPremiumStatusUseCase(
    wallrRepository: WallrRepository
  ): UserPremiumStatusUseCase = UserPremiumStatusInteractor(wallrRepository)

  @Provides
  @Singleton
  fun provideSearchPicturesUseCase(
    wallrRepository: WallrRepository
  ): SearchPicturesUseCase = SearchPicturesInteractor(wallrRepository)

  @Provides
  fun provideWallpaperUseCase(
    wallrRepository: WallrRepository
  ): WallpaperImagesUseCase = WallpaperImagesInteractor(wallrRepository)

  @Provides
  fun provideShareImagesUseCase(
    wallrRepository: WallrRepository
  ): ImageOptionsUseCase = ImageOptionsInteractor(wallrRepository)

  @Provides
  fun provideMinimalImagesUseCase(
    wallrRepository: WallrRepository
  ): MinimalImagesUseCase = MinimalImagesInteractor(wallrRepository)

  @Provides
  fun provideColorsDetailsUseCase(
    wallrRepository: WallrRepository
  ): ColorImagesUseCase = ColorImagesInteractor(wallrRepository)

  @Provides
  fun provideWidgetHintsUseCase(
    wallrRepository: WallrRepository
  ): WidgetHintsUseCase = WidgetHintsInteractor(wallrRepository)

  @Provides
  fun provideCollectionImagesUseCase(
    serviceManager: ServiceManager,
    wallrRepository: WallrRepository
  ): CollectionImagesUseCase = CollectionsImagesInteractor(serviceManager, wallrRepository)

  @Provides
  fun provideAutomaticWallpaperChangerUseCase(
    wallpaperSetter: WallpaperSetter,
    wallrRepository: WallrRepository,
    resourceUtils: ResourceUtils,
    executionThread: ExecutionThread,
    postExecutionThread: PostExecutionThread,
    timeManager: TimeManager
  ): AutomaticWallpaperChangerUseCase = AutomaticWallpaperChangerInteractor(wallpaperSetter,
      wallrRepository, resourceUtils, executionThread, postExecutionThread, timeManager)

  @Provides
  fun provideImageRecyclerViewPresenter()
      : ImageRecyclerViewPresenter = ImageRecyclerViewPresenterImpl()

  @Provides
  fun provideDragSelectRecyclerItemPresenter(): DragSelectItemPresenter = DragSelectRecyclerPresenterImpl()

  @Provides
  fun provideCollectionRecyclerPresenter(): CollectionRecyclerPresenter = CollectionRecyclerPresenterImpl()

  @Provides
  fun provideServiceManager(context: Context): ServiceManager = ServiceManagerImpl(context)

  @Provides
  fun provideNotificationFactory(context: Context)
      : NotificationFactory = NotificationFactoryImpl(context)

  @Provides
  fun providePermissionsCheckerHelper(context: Context)
      : PermissionsCheckerHelper = PermissionsCheckerHelperImpl(context)
}