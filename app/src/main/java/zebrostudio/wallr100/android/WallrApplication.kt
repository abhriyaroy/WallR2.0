package zebrostudio.wallr100.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.bumptech.glide.request.target.ViewTarget
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.di.DaggerAppComponent
import javax.inject.Inject

class WallrApplication : Application(), HasActivityInjector {

  @Inject
  internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

  override fun onCreate() {
    super.onCreate()
    DaggerAppComponent.builder()
        .application(this)
        .build()
        .inject(this)
    ViewTarget.setTagId(R.id.glide_tag)
  }

  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

  override fun activityInjector() = activityDispatchingAndroidInjector

}