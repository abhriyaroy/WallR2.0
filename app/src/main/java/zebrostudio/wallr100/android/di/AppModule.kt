package zebrostudio.wallr100.android.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.AndroidBackgroundThreads
import zebrostudio.wallr100.android.AndroidMainThread
import zebrostudio.wallr100.android.di.scopes.PerApplication
import zebrostudio.wallr100.android.notification.NotificationFactory
import zebrostudio.wallr100.android.notification.NotificationFactoryImpl
import zebrostudio.wallr100.android.permissions.PermissionsChecker
import zebrostudio.wallr100.android.permissions.PermissionsCheckerImpl
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

@Module
class AppModule {

  @Provides
  @PerApplication
  fun provideContext(application: Application): Context {
    return application
  }

  @Provides
  @PerApplication
  fun provideSystemInfo(): SystemInfoProvider = SystemInfoProviderImpl()

  @Provides
  @PerApplication
  fun provideResourceUtils(context: Context): ResourceUtils = ResourceUtilsImpl(context)

  @Provides
  @PerApplication
  fun provideFragmentTag(resourceUtils: ResourceUtils): FragmentNameTagFetcher =
    FragmentNameTagFetcherImpl(
        resourceUtils
    )

  @Provides
  @PerApplication
  fun provideSharedPrefsHelper(context: Context): SharedPrefsHelper = SharedPrefsHelperImpl(context)

  @Provides
  @PerApplication
  fun providesDatabaseHelper(context: Context): DatabaseHelper =
    DatabaseHelperImpl(context)

  @Provides
  @PerApplication
  fun provideFirebaseDatabaseHelper(context: Context): FirebaseDatabaseHelper =
    FirebaseDatabaseHelperImpl(context)

  @Provides
  @PerApplication
  fun provideMinimalColorHelper(
    context: Context,
    sharedPrefsHelper: SharedPrefsHelper
  ): MinimalColorHelper = MinimalColorsHelperImpl(context, sharedPrefsHelper)

  @Provides
  @PerApplication
  fun provideGson(): GsonProvider = GsonProviderImpl()

  @Provides
  @PerApplication
  fun provideAndroidMainThread(): PostExecutionThread = AndroidMainThread()

  @Provides
  @PerApplication
  fun provideAndroidBackgroundThread(): ExecutionThread = AndroidBackgroundThreads()

  @Provides
  @PerApplication
  fun provideRemoteAuthServiceFactory(): RemoteAuthServiceFactory =
    RemoteAuthServiceFactoryImpl()

  @Provides
  @PerApplication
  fun provideUnsplashClientFactory(): UnsplashClientFactory = UnsplashClientFactoryImpl()

  @Provides
  @PerApplication
  fun provideCollectionsDatabaseImageEntityMapper():
      CollectionsDatabaseImageEntityMapper = CollectionsDatabaseImageEntityMapperImpl()

  @Provides
  @PerApplication
  fun provideDataBaseImageTypeMapper(): DatabaseImageTypeMapper = DatabaseImageTypeMapperImpl()

  @Provides
  @PerApplication
  fun provideUnsplashPictureEntityMapper(): UnsplashPictureEntityMapper =
    UnsplashPictureEntityMapperImpl()

  @Provides
  @PerApplication
  fun provideFirebasePictureEntityMapper(): FirebasePictureEntityMapper =
    FirebasePictureEntityMapperImpl()

  @Provides
  @PerApplication
  fun provideUrlShortener(): UrlShortener = UrlShortenerImpl()

  @Provides
  @PerApplication
  fun provideFileHandler(context: Context): FileHandler = FileHandlerImpl(context)

  @Provides
  @PerApplication
  fun provideWallpaperSetter(context: Context): WallpaperSetter = WallpaperSetterImpl(context)

  @Provides
  @PerApplication
  fun provideDownloadHelper(
    context: Context,
    fileHandler: FileHandler
  ): DownloadHelper = DownloadHelperImpl(context, fileHandler)

  @Provides
  @PerApplication
  fun provideImageHandler(
    context: Context,
    fileHandler: FileHandler,
    databaseHelper: DatabaseHelper,
    wallpaperSetter: WallpaperSetter
  ): ImageHandler = ImageHandlerImpl(context, fileHandler, databaseHelper, wallpaperSetter)

  @Provides
  @PerApplication
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
  ): WallrRepository = WallrDataRepository(
      retrofitFirebaseAuthFactory,
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
      executionThread
  )

  @Provides
  @PerApplication
  fun providesTimeManager(): TimeManager = TimeManagerImpl()

  @Provides
  @PerApplication
  fun provideAuthenticatePurchaseUseCase(
    wallrRepository: WallrRepository
  ): AuthenticatePurchaseUseCase = AuthenticatePurchaseInteractor(wallrRepository)

  @Provides
  @PerApplication
  fun provideUserPremiumStatusUseCase(
    wallrRepository: WallrRepository
  ): UserPremiumStatusUseCase = UserPremiumStatusInteractor(wallrRepository)

  @Provides
  @PerApplication
  fun provideSearchPicturesUseCase(
    wallrRepository: WallrRepository
  ): SearchPicturesUseCase = SearchPicturesInteractor(wallrRepository)

  @Provides
  @PerApplication
  fun provideWallpaperUseCase(
    wallrRepository: WallrRepository
  ): WallpaperImagesUseCase = WallpaperImagesInteractor(wallrRepository)

  @Provides
  @PerApplication
  fun provideShareImagesUseCase(
    wallrRepository: WallrRepository
  ): ImageOptionsUseCase = ImageOptionsInteractor(wallrRepository)

  @Provides
  @PerApplication
  fun provideMinimalImagesUseCase(
    wallrRepository: WallrRepository
  ): MinimalImagesUseCase = MinimalImagesInteractor(wallrRepository)

  @Provides
  @PerApplication
  fun provideColorsDetailsUseCase(
    wallrRepository: WallrRepository
  ): ColorImagesUseCase = ColorImagesInteractor(wallrRepository)

  @Provides
  @PerApplication
  fun provideWidgetHintsUseCase(
    wallrRepository: WallrRepository
  ): WidgetHintsUseCase = WidgetHintsInteractor(wallrRepository)

  @Provides
  @PerApplication
  fun provideCollectionImagesUseCase(
    serviceManager: ServiceManager,
    wallrRepository: WallrRepository
  ): CollectionImagesUseCase = CollectionsImagesInteractor(serviceManager, wallrRepository)

  @Provides
  @PerApplication
  fun provideAutomaticWallpaperChangerUseCase(
    wallpaperSetter: WallpaperSetter,
    wallrRepository: WallrRepository,
    resourceUtils: ResourceUtils,
    executionThread: ExecutionThread,
    postExecutionThread: PostExecutionThread,
    timeManager: TimeManager
  ): AutomaticWallpaperChangerUseCase = AutomaticWallpaperChangerInteractor(
      wallpaperSetter,
      wallrRepository, resourceUtils, executionThread, postExecutionThread, timeManager
  )

  @Provides
  @PerApplication
  fun provideImageRecyclerViewPresenter()
      : ImageRecyclerViewPresenter = ImageRecyclerViewPresenterImpl()

  @Provides
  @PerApplication
  fun provideDragSelectRecyclerItemPresenter(): DragSelectItemPresenter =
    DragSelectRecyclerPresenterImpl()

  @Provides
  @PerApplication
  fun provideCollectionRecyclerPresenter(): CollectionRecyclerPresenter =
    CollectionRecyclerPresenterImpl()

  @Provides
  @PerApplication
  fun provideServiceManager(context: Context): ServiceManager = ServiceManagerImpl(context)

  @Provides
  @PerApplication
  fun provideNotificationFactory(context: Context)
      : NotificationFactory = NotificationFactoryImpl(context)

  @Provides
  @PerApplication
  fun providePermissionsCheckerHelper(context: Context)
      : PermissionsChecker = PermissionsCheckerImpl(context)
}