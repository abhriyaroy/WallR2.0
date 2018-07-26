package zebrostudio.wallr100.android.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import org.buffer.android.boilerplate.domain.executor.PostExecutionThread
import zebrostudio.wallr100.android.AndroidMainThread
import zebrostudio.wallr100.android.di.scopes.PerApplication
import zebrostudio.wallr100.data.WallrDataRepository
import zebrostudio.wallr100.data.api.RemoteServiceFactory
import zebrostudio.wallr100.data.mapper.PurchaseAuthResponseMapper
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.interactor.AuthenticatePurchaseUseCase

@Module
class AppModule {

  @Provides
  @PerApplication
  fun provideContext(application: Application): Context {
    return application
  }

  @Provides
  @PerApplication
  fun provideAndroidMainThread(): PostExecutionThread = AndroidMainThread()

  @Provides
  @PerApplication
  fun provideRemoteServiceFactory(): RemoteServiceFactory = RemoteServiceFactory()

  @Provides
  @PerApplication
  fun provideAuthResponseMapper(): PurchaseAuthResponseMapper = PurchaseAuthResponseMapper()

  @Provides
  @PerApplication
  fun provideWallrRepository(
    remoteServiceFactory: RemoteServiceFactory,
    purchaseAuthResponseMapper: PurchaseAuthResponseMapper
  ): WallrRepository = WallrDataRepository(remoteServiceFactory, purchaseAuthResponseMapper)

  @Provides
  @PerApplication
  fun provideAuthenticatePurchaseUseCase(
    wallrRepository: WallrRepository,
    postExecutionThread: PostExecutionThread
  ): AuthenticatePurchaseUseCase = AuthenticatePurchaseUseCase(wallrRepository, postExecutionThread)

}