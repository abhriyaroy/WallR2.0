package zebrostudio.wallr100

import android.app.Activity
import android.app.Application
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import zebrostudio.wallr100.di.DaggerAppComponent
import javax.inject.Inject

class WallrApplication : Application(), HasActivityInjector {

  @Inject
  internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

  override fun activityInjector() = activityDispatchingAndroidInjector

  override fun onCreate() {
    super.onCreate()
    DaggerAppComponent.builder()
        .application(this)
        .build()
        .inject(this)
  }

}