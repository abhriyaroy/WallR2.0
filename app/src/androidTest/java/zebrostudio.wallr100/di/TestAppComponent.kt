package zebrostudio.wallr100.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import zebrostudio.wallr100.android.WallrApplication
import zebrostudio.wallr100.android.di.ActivityBuilder
import zebrostudio.wallr100.android.di.AppComponent
import zebrostudio.wallr100.android.di.ServiceBuilder
import zebrostudio.wallr100.android.di.scopes.PerApplication

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
}