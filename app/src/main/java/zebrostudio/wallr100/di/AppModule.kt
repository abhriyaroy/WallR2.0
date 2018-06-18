package zebrostudio.wallr100.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.data.DataRepository
import javax.inject.Singleton

@Module
class AppModule{

  @Provides
  @Singleton
  internal fun provideContext(application: Application): Context = application

  @Provides
  @Singleton
  internal fun provideDataRepository() : DataRepository = DataRepository()

}