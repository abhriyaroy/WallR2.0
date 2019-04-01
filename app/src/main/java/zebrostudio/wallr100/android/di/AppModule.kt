package zebrostudio.wallr100.android.di

import android.app.Application
import android.content.Context
import com.pddstudio.urlshortener.URLShortener
import com.pddstudio.urlshortener.URLShortenerImpl
import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.android.AndroidBackgroundThreads
import zebrostudio.wallr100.android.AndroidMainThread
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcherImpl
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
import zebrostudio.wallr100.data.GsonDataHelper
import zebrostudio.wallr100.data.GsonDataHelperImpl
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
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapper
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapper
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.ExecutionThread
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseInteractor
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
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
import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerContract.DragSelectItemPresenter
import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerIPresenterImpl
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl
import zebrostudio.wallr100.presentation.detail.GsonHelper
import zebrostudio.wallr100.presentation.detail.GsonHelperImpl
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
  fun provideGsonDataHelper(): GsonDataHelper = GsonDataHelperImpl()

  @Provides
  @Singleton
  fun provideAndroidMainThread(): PostExecutionThread = AndroidMainThread()

  @Provides
  @Singleton
  fun provideAndroidBackgroundThread(): ExecutionThread = AndroidBackgroundThreads()

  @Provides
  @Singleton
  fun providesGsonHelper(): GsonHelper = GsonHelperImpl()

  @Provides
  @Singleton
  fun provideRemoteAuthServiceFactory(): RemoteAuthServiceFactory =
      RemoteAuthServiceFactoryImpl()

  @Provides
  @Singleton
  fun provideUnsplashClientFactory(): UnsplashClientFactory = UnsplashClientFactoryImpl()

  @Provides
  fun providePictureEntityMapper(): UnsplashPictureEntityMapper = UnsplashPictureEntityMapper()

  @Provides
  fun provideFirebasePictureEntityMapper(): FirebasePictureEntityMapper = FirebasePictureEntityMapper()

  @Provides
  fun provideUrlShortener(): URLShortener = URLShortenerImpl()

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
    fileHandler: FileHandler,
    databaseHelper: DatabaseHelper
  ): ImageHandler = ImageHandlerImpl(context, fileHandler, databaseHelper)

  @Provides
  @Singleton
  fun provideWallrRepository(
    retrofitFirebaseAuthFactory: RemoteAuthServiceFactory,
    unsplashClientFactory: UnsplashClientFactory,
    sharedPrefsHelper: SharedPrefsHelper,
    gsonDataHelper: GsonDataHelper,
    unsplashPictureEntityMapper: UnsplashPictureEntityMapper,
    firebaseDatabaseHelper: FirebaseDatabaseHelper,
    firebasePictureEntityMapper: FirebasePictureEntityMapper,
    urlShortener: URLShortener,
    imageHandler: ImageHandler,
    fileHandler: FileHandler,
    downloadHelper: DownloadHelper,
    minimalColorHelper: MinimalColorHelper,
    executionThread: ExecutionThread
  ): WallrRepository = WallrDataRepository(retrofitFirebaseAuthFactory,
      unsplashClientFactory,
      sharedPrefsHelper,
      gsonDataHelper,
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
  fun provideImageRecyclerViewPresenter()
      : ImageRecyclerItemContract.ImageRecyclerViewPresenter = ImageRecyclerViewPresenterImpl()

  @Provides
  fun provideDragSelectRecyclerItemPresenter(): DragSelectItemPresenter = DragSelectRecyclerIPresenterImpl()

}