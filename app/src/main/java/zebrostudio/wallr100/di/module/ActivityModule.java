package zebrostudio.wallr100.di.module;

import android.app.Activity;
import android.content.Context;

import com.crashlytics.android.Crashlytics;

import dagger.Module;
import dagger.Provides;
import zebrostudio.wallr100.di.ActivityContext;

@Module
public class ActivityModule {
    private final Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    @ActivityContext
    Context provideActivityContext() {
        return mActivity;
    }

    @Provides
    @ActivityContext
    public Activity providesActivity(){
        return mActivity;
    }

    @Provides
    Crashlytics providesCrashlytics(){
        return new Crashlytics();
    }

}
