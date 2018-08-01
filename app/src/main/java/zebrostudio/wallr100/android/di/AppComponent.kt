package zebrostudio.wallr100.android.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import zebrostudio.wallr100.android.WallrApplication
import zebrostudio.wallr100.android.di.scopes.PerApplication

@PerApplication
@Component(
    modules = [(AndroidSupportInjectionModule::class), (AppModule::class), (ActivityBuilder::class)])
interface AppComponent {

  @Component.Builder
  interface Builder {

    @BindsInstance
    fun application(application: Application): Builder

    fun build(): AppComponent
  }

  fun inject(app: WallrApplication)

}