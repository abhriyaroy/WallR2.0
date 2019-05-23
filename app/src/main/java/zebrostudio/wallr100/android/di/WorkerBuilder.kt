package zebrostudio.wallr100.android.di

import androidx.work.ListenableWorker
import androidx.work.Worker
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerService
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerServiceImpl
import zebrostudio.wallr100.android.service.AutomaticWallpaperChangerWorkerFactory
import kotlin.reflect.KClass

@@MapKey
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class WorkerKey(val value: KClass<out ListenableWorker>)

@Module
abstract class WorkerBuilder {

  @Module
  interface WorkerBindingModule {
    @Binds
    @IntoMap
    @WorkerKey(AutomaticWallpaperChangerServiceImpl::class)
    fun bindHelloWorldWorker(factory: AutomaticWallpaperChangerServiceImpl.Factory): AutomaticWallpaperChangerWorkerFactory.ChildWorkerFactory
  }

}