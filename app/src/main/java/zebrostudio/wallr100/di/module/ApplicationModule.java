package zebrostudio.wallr100.di.module;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;
import zebrostudio.wallr100.di.ApplicationContext;

@Module
public class ApplicationModule {
    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideApplicationContext() {
        return mApplication;
    }
}
