package zebrostudio.wallr100.android.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.android.AndroidMainThread
import zebrostudio.wallr100.android.di.scopes.PerApplication
import zebrostudio.wallr100.data.SharedPrefsHelper
import zebrostudio.wallr100.data.SharedPrefsHelperImpl
import zebrostudio.wallr100.data.WallrDataRepository
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactoryImpl
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
  fun provideSharedPrefsHelper(context: Context): SharedPrefsHelper = SharedPrefsHelperImpl(context)

  @Provides
  @PerApplication
  fun provideAndroidMainThread(): PostExecutionThread = AndroidMainThread()

  @Provides
  @PerApplication
  fun provideRemoteAuthServiceFactory(): RemoteAuthServiceFactory = RemoteAuthServiceFactoryImpl()

  @Provides
  @PerApplication
  fun provideWallrRepository(
    retrofitFirebaseAuthFactory: RemoteAuthServiceFactory,
    sharedPrefsHelper: SharedPrefsHelper
  ): WallrRepository = WallrDataRepository(retrofitFirebaseAuthFactory,
      sharedPrefsHelper)

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