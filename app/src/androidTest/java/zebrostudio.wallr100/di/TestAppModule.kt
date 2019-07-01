package zebrostudio.wallr100.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
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
import zebrostudio.wallr100.android.ui.ImageLoader
import zebrostudio.wallr100.android.ui.ImageLoaderImpl
import zebrostudio.wallr100.android.utils.*
import zebrostudio.wallr100.data.*
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactoryImpl
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UnsplashClientFactoryImpl
import zebrostudio.wallr100.data.database.DatabaseHelper
import zebrostudio.wallr100.data.database.DatabaseHelperImpl
import zebrostudio.wallr100.data.mapper.*
import zebrostudio.wallr100.data.urlshortener.UrlShortener
import zebrostudio.wallr100.data.urlshortener.UrlShortenerImpl
import zebrostudio.wallr100.domain.TimeManager
import zebrostudio.wallr100.domain.TimeManagerImpl
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.ExecutionThread
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.*
import zebrostudio.wallr100.presentation.adapters.*

@Module
class TestAppModule {

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
      FragmentNameTagFetcherImpl(resourceUtils)

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
  fun provideImageRecyclerViewPresenter()
      : ImageRecyclerItemContract.ImageRecyclerViewPresenter = ImageRecyclerViewPresenterImpl()

  @Provides
  @PerApplication
  fun provideDragSelectRecyclerItemPresenter(): DragSelectRecyclerContract.DragSelectItemPresenter =
      DragSelectRecyclerPresenterImpl()

  @Provides
  @PerApplication
  fun provideCollectionRecyclerPresenter(): CollectionRecyclerContract.CollectionRecyclerPresenter =
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

  @Provides
  @PerApplication
  fun providesImageLoader(): ImageLoader = ImageLoaderImpl()

  @Provides
  @PerApplication
  fun provideWallrRepository(): WallrRepository = Mockito.mock(WallrRepository::class.java)

}