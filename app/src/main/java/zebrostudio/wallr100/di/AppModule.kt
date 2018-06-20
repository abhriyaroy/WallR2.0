package zebrostudio.wallr100.di

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.data.DataRepository
import javax.inject.Singleton

@Module
class AppModule{

  @Provides
  @Singleton
  internal fun provideDataRepository() : DataRepository = DataRepository()

}