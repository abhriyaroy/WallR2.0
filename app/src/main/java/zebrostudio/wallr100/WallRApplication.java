package zebrostudio.wallr100;

import android.content.Context;
import android.support.multidex.MultiDex;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.di.component.AppComponent;
import zebrostudio.wallr100.di.component.DaggerAppComponent;

@Singleton
public class WallRApplication extends DaggerApplication {

    @Inject
    DataManager mDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mDataManager.requestFirebasePersistenceInitialization(this);
        mDataManager.requestOneSignalSdkInitialization(this);
        mDataManager.requestGlideTagConfiguration();
        mDataManager.requestAutomaticWallpaperChangerJobInitialization();
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

}
