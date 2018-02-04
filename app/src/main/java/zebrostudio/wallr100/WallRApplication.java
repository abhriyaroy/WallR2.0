package zebrostudio.wallr100;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.bumptech.glide.request.target.ViewTarget;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import zebrostudio.wallr100.di.component.AppComponent;
import zebrostudio.wallr100.di.component.DaggerAppComponent;

public class WallRApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        configureFirebasePersistance();
        configureOneSignalSdk();
        configureGlideTag();
        configureAutomaticWallpaperChangerJob();
        /*mApplicationComponent = DaggerApplicationComponent.builder()
                .build();
        mApplicationComponent.inject(this);*/

    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
        appComponent.inject(this);
        return appComponent;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void configureFirebasePersistance() {
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.setPersistenceEnabled(true);
        }
    }

    private void configureOneSignalSdk() {
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    private void configureGlideTag() {
        ViewTarget.setTagId(R.id.glide_tag);
    }

    private void configureAutomaticWallpaperChangerJob() {
        // needs to be configured
    }
}
