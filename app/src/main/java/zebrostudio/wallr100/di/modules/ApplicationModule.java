package zebrostudio.wallr100.di.modules;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;
import zebrostudio.wallr100.WallRApplication;
import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.data.FireBaseManager;
import zebrostudio.wallr100.di.ApplicationContext;
import zebrostudio.wallr100.utils.SharedPrefsUtils;

@Module
public abstract class ApplicationModule {

    @ApplicationContext
    @Binds
    abstract Context provideContext(Application application);

    @Singleton
    @Provides
    static SharedPrefsUtils providesSharedPrefsUtils(@ApplicationContext Context context) {
        return new SharedPrefsUtils(context);
    }

    @Provides
    static CompositeDisposable providesCompositeDisposable() {
        return new CompositeDisposable();
    }

}
