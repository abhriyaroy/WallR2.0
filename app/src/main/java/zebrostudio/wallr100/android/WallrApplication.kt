package zebrostudio.wallr100.android

import android.app.Activity
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import android.support.multidex.MultiDex
import com.bumptech.glide.request.target.ViewTarget
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.di.DaggerAppComponent
import javax.inject.Inject

const val NOTIFICATION_CHANNEL_ID = "WallrNotificationChannel"
const val NOTIFICATION_CHANNEL_NAME = "WallrNotification"

class WallrApplication : Application(), HasActivityInjector, HasServiceInjector {

  @Inject
  internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
  @Inject
  internal lateinit var serviceDispatchingAndroidInjector: DispatchingAndroidInjector<Service>

  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

  override fun onCreate() {
    super.onCreate()
    DaggerAppComponent.builder()
        .application(this)
        .build()
        .inject(this)
    ViewTarget.setTagId(R.id.glide_tag)
    createNotificationChannel()
  }

  override fun activityInjector() = activityDispatchingAndroidInjector

  override fun serviceInjector() = serviceDispatchingAndroidInjector

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val serviceChannel = NotificationChannel(
          NOTIFICATION_CHANNEL_ID,
          NOTIFICATION_CHANNEL_NAME,
          NotificationManager.IMPORTANCE_HIGH
      )
      getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
    }
  }

}