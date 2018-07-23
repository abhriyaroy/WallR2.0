package zebrostudio.wallr100.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.data.DataRepository
import zebrostudio.wallr100.data.network.NetworkManager
import zebrostudio.wallr100.data.purchase.PurchaseHelper
import javax.inject.Singleton

@Module
class AppModule {

  @Provides
  @Singleton
  internal fun provideContext(application: Application): Context = application

  @Singleton
  @Provides
  internal fun provideDataRepository(purchaseHelper: PurchaseHelper, networkManager: NetworkManager)
      : DataRepository = DataRepository(purchaseHelper, networkManager)

  @Singleton
  @Provides
  internal fun providePurchaseHelper(context: Context): PurchaseHelper = PurchaseHelper(context)

  @Singleton
  @Provides
  internal fun provideNetworkUtils(context: Context): NetworkManager = NetworkManager(context)

}