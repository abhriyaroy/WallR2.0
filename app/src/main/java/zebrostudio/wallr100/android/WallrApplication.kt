package zebrostudio.wallr100.android

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import androidx.multidex.MultiDex
import com.onesignal.OneSignal
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import zebrostudio.wallr100.android.di.DaggerAppComponent
import zebrostudio.wallr100.secrets.ApiKeys.ONE_SIGNAL_API_KEY
import javax.inject.Inject

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
    initPushNotifications()
  }

  override fun activityInjector() = activityDispatchingAndroidInjector

  override fun serviceInjector() = serviceDispatchingAndroidInjector

  private fun initPushNotifications() {
      OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

      // OneSignal Initialization
      OneSignal.initWithContext(this);
      OneSignal.setAppId(ONE_SIGNAL_API_KEY);

      // promptForPushNotifications will show the native Android notification permission prompt.
      // We recommend removing the following code and instead using an In-App Message to prompt for notification permission (See step 7)
      OneSignal.promptForPushNotifications();
//    OneSignal.startInit(this)
//        .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//        .unsubscribeWhenNotificationsAreDisabled(true)
//        .init()
  }

}
