package zebrostudio.wallr100;

import android.app.Application;

import zebrostudio.wallr100.di.component.ApplicationComponent;

public class WallRApplication extends Application {

    ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();


    }
}
