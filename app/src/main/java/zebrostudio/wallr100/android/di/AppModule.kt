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
import zebrostudio.wallr100.data.api.RemoteServiceFactory
import zebrostudio.wallr100.data.mapper.ProAuthMapperImpl
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase
import zebrostudio.wallr100.presentation.mapper.ProAuthPresentationMapperImpl

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
  fun provideRemoteServiceFactory(): RemoteServiceFactory = RemoteServiceFactory()

  @Provides
  fun provideAuthResponseMapper(): ProAuthMapperImpl = ProAuthMapperImpl()

  @Provides
  fun provideProAuthPresentationMapper(): ProAuthPresentationMapperImpl = ProAuthPresentationMapperImpl()

  @Provides
  @PerApplication
  fun provideWallrRepository(
    remoteServiceFactory: RemoteServiceFactory,
    mapperImpl: ProAuthMapperImpl
  ): WallrRepository = WallrDataRepository(remoteServiceFactory, mapperImpl)

  @Provides
  @PerApplication
  fun provideAuthenticatePurchaseUseCase(
    wallrRepository: WallrRepository,
    postExecutionThread: PostExecutionThread
  ): AuthenticatePurchaseUseCase = AuthenticatePurchaseUseCase(wallrRepository, postExecutionThread)

}