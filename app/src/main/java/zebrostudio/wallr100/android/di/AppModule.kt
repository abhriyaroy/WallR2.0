package zebrostudio.wallr100.android.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.android.AndroidMainThread
import zebrostudio.wallr100.android.di.scopes.PerApplication
import zebrostudio.wallr100.data.SharedPrefsHelper
import zebrostudio.wallr100.data.WallrDataRepository
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.mapper.PictureEntityMapper
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase

@Module
class AppModule {

  @Provides
  @PerApplication
  fun provideContext(application: Application): Context {
    return application
  }

  @Provides
  @PerApplication
  fun provideSharedPrefsHelper(context: Context): SharedPrefsHelper = SharedPrefsHelper(context)

  @Provides
  @PerApplication
  fun provideAndroidMainThread(): PostExecutionThread = AndroidMainThread()

  @Provides
  @PerApplication
  fun provideRemoteAuthServiceFactory(): RemoteAuthServiceFactory = RemoteAuthServiceFactory()

  @Provides
  @PerApplication
  fun provideUnsplashClientFactory(): UnsplashClientFactory = UnsplashClientFactory()

  @Provides
  fun providePictureEntityMapper(): PictureEntityMapper = PictureEntityMapper()

  @Provides
  @PerApplication
  fun provideWallrRepository(
    retrofitFirebaseAuthFactory: RemoteAuthServiceFactory,
    unsplashClientFactory: UnsplashClientFactory,
    sharedPrefsHelper: SharedPrefsHelper,
    pictureEntityMapper: PictureEntityMapper
  ): WallrRepository = WallrDataRepository(retrofitFirebaseAuthFactory,
      unsplashClientFactory,
      sharedPrefsHelper,
      pictureEntityMapper)

  @Provides
  @PerApplication
  fun provideAuthenticatePurchaseUseCase(
    wallrRepository: WallrRepository,
    postExecutionThread: PostExecutionThread
  ): AuthenticatePurchaseUseCase = AuthenticatePurchaseUseCase(wallrRepository, postExecutionThread)

  @Provides
  @PerApplication
  fun provideUserPremiumStatusUseCase(
    wallrRepository: WallrRepository
  ): UserPremiumStatusUseCase = UserPremiumStatusUseCase(wallrRepository)

}