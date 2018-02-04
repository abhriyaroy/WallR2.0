package zebrostudio.wallr100.ui.main;

import com.crashlytics.android.Crashlytics;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import zebrostudio.wallr100.utils.GuillotineUtils;

@Module
public abstract class MainActivityModule {

    @Binds
    abstract MainActivityPresenterImpl bindMainActivityPresernterImpl(
            MainActivityPresenterImpl mainActivityPresenter);

    @Provides
    static Crashlytics providesCrashlytics(){
        return new Crashlytics();
    }

}
