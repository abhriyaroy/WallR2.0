package zebrostudio.wallr100.android

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.support.multidex.MultiDex
import com.onesignal.OneSignal
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import zebrostudio.wallr100.android.di.AppComponent
import zebrostudio.wallr100.android.di.DaggerAppComponent
import javax.inject.Inject

class WallrApplication : Application(), HasActivityInjector, HasServiceInjector {

  @Inject
  internal lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
  @Inject
  internal lateinit var serviceDispatchingAndroidInjector: DispatchingAndroidInjector<Service>
  private lateinit var appComponent: AppComponent

  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    MultiDex.install(this)
  }

  override fun onCreate() {
    super.onCreate()
    initAppComponent()
    initPushNotifications()
  }

  override fun activityInjector() = activityDispatchingAndroidInjector

  override fun serviceInjector() = serviceDispatchingAndroidInjector

  fun setAppComponent(appComponent: AppComponent) {
    this.appComponent = appComponent
  }

  private fun initPushNotifications() {
    OneSignal.startInit(this)
        .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
        .unsubscribeWhenNotificationsAreDisabled(true)
        .init()
  }

  private fun initAppComponent() {
    appComponent = DaggerAppComponent.builder()
        .application(this)
        .build()
    appComponent.inject(this)
  }

  companion object {
    fun getApplication(context: Context): WallrApplication {
      return context.applicationContext as WallrApplication
    }
  }

}