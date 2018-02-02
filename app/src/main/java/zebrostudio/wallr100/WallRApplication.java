package zebrostudio.wallr100;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.bumptech.glide.request.target.ViewTarget;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;
import com.evernote.android.job.JobManager;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;

import zebrostudio.wallr100.di.component.ApplicationComponent;
import zebrostudio.wallr100.di.component.DaggerApplicationComponent;

public class WallRApplication extends Application {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        configureFirebasePersistance();
        configureOneSignalSdk();
        configureGlideTag();
        configureAutomaticWallpaperChangerJob();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .build();
        mApplicationComponent.inject(this);

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

    private ApplicationComponent getApplicationComponent(){
        return mApplicationComponent;
    }
}
