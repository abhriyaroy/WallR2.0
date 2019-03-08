package zebrostudio.wallr100.android.di

import android.app.Application
import android.content.Context
import com.pddstudio.urlshortener.URLShortener
import com.pddstudio.urlshortener.URLShortenerImpl
import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.AndroidBackgroundThreads
import zebrostudio.wallr100.android.AndroidMainThread
import zebrostudio.wallr100.android.di.scopes.PerApplication
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcherImpl
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.ResourceUtilsImpl
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.android.utils.WallpaperSetterImpl
import zebrostudio.wallr100.data.database.DatabaseHelper
import zebrostudio.wallr100.data.database.DatabaseHelperImpl
import zebrostudio.wallr100.data.DownloadHelper
import zebrostudio.wallr100.data.DownloadHelperImpl
import zebrostudio.wallr100.data.FileHandler
import zebrostudio.wallr100.data.FileHandlerImpl
import zebrostudio.wallr100.data.FirebaseDatabaseHelper
import zebrostudio.wallr100.data.FirebaseDatabaseHelperImpl
import zebrostudio.wallr100.data.ImageHandler
import zebrostudio.wallr100.data.ImageHandlerImpl
import zebrostudio.wallr100.data.SharedPrefsHelper
import zebrostudio.wallr100.data.SharedPrefsHelperImpl
import zebrostudio.wallr100.data.WallrDataRepository
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactoryImpl
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UnsplashClientFactoryImpl
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapper
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapper
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.ExecutionThread
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseInteractor
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.interactor.ImageOptionsInteractor
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.interactor.SearchPicturesInteractor
import zebrostudio.wallr100.domain.interactor.SearchPicturesUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusInteractor
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.interactor.WallpaperImagesInteractor
import zebrostudio.wallr100.domain.interactor.WallpaperImagesUseCase
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract
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
  fun provideResourceUtils(context: Context): ResourceUtils = ResourceUtilsImpl(context)

  @Provides
  @PerApplication
  fun provideFragmentTag(resourceUtils: ResourceUtils): FragmentNameTagFetcher = FragmentNameTagFetcherImpl(
      resourceUtils)

  @Provides
  @PerApplication
  fun provideSharedPrefsHelper(context: Context): SharedPrefsHelper = SharedPrefsHelperImpl(context)

  @Provides
  @PerApplication
  fun providesDatabaseHelper(context: Context): DatabaseHelper = DatabaseHelperImpl(
      context)

  @Provides
  @PerApplication
  fun provideFirebaseDatabaseHelper(context: Context): FirebaseDatabaseHelper =
      FirebaseDatabaseHelperImpl(context)

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
  fun providePictureEntityMapper(): UnsplashPictureEntityMapper = UnsplashPictureEntityMapper()

  @Provides
  fun provideFirebasePictureEntityMapper(): FirebasePictureEntityMapper = FirebasePictureEntityMapper()

  @Provides
  fun provideUrlShorterner(): URLShortener = URLShortenerImpl()

  @Provides
  fun provideFileHandler(): FileHandler = FileHandlerImpl()

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
    fileHandler: FileHandler
  ): ImageHandler = ImageHandlerImpl(context, fileHandler)

  @Provides
  @PerApplication
  fun provideWallrRepository(
    retrofitFirebaseAuthFactory: RemoteAuthServiceFactory,
    unsplashClientFactory: UnsplashClientFactory,
    sharedPrefsHelper: SharedPrefsHelper,
    unsplashPictureEntityMapper: UnsplashPictureEntityMapper,
    firebaseDatabaseHelper: FirebaseDatabaseHelper,
    firebasePictureEntityMapper: FirebasePictureEntityMapper,
    urlShortener: URLShortener,
    imageHandler: ImageHandler,
    fileHandler: FileHandler,
    downloadHelper: DownloadHelper,
    executionThread: ExecutionThread
  ): WallrRepository = WallrDataRepository(retrofitFirebaseAuthFactory,
      unsplashClientFactory,
      sharedPrefsHelper,
      unsplashPictureEntityMapper,
      firebaseDatabaseHelper,
      firebasePictureEntityMapper,
      urlShortener,
      imageHandler,
      fileHandler,
      downloadHelper,
      executionThread)

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
  fun provideWallpaperUseCase(
    wallrRepository: WallrRepository
  ): WallpaperImagesUseCase = WallpaperImagesInteractor(wallrRepository)

  @Provides
  fun provideShareImagesUseCase(
    wallrRepository: WallrRepository
  ): ImageOptionsUseCase = ImageOptionsInteractor(wallrRepository)

  @Provides
  fun provideImageRecyclerViewPresenter()
      : ImageRecyclerItemContract.ImageRecyclerViewPresenter = ImageRecyclerViewPresenterImpl()

}