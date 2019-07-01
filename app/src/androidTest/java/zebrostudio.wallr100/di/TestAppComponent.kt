package zebrostudio.wallr100.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import zebrostudio.wallr100.TestBase
import zebrostudio.wallr100.android.di.ActivityBuilder
import zebrostudio.wallr100.android.di.AppComponent
import zebrostudio.wallr100.android.di.ServiceBuilder
import zebrostudio.wallr100.android.di.scopes.PerApplication
import zebrostudio.wallr100.domain.WallrRepository

@PerApplication
@Component(modules = [(AndroidInjectionModule::class), (TestAppModule::class),
  (ActivityBuilder::class), (ServiceBuilder::class)])
interface TestAppComponent : AppComponent {
  @Component.Builder
  interface Builder {

    @BindsInstance
    fun application(application: Application): Builder

    fun build(): TestAppComponent
  }

  val wallrRepository: WallrRepository
}