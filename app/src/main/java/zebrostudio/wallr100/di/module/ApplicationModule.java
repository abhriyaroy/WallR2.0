package zebrostudio.wallr100.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import zebrostudio.wallr100.WallRApplication;
import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.utils.FragmentTags;
import zebrostudio.wallr100.utils.SharedPrefsUtils;

@Module
public abstract class ApplicationModule {

    @Binds
    @Singleton
    abstract Context provideContext(Application application);

    @Provides
    @Singleton
    static SharedPrefsUtils providesSharedPrefsUtils(WallRApplication application){
        return new SharedPrefsUtils(application);
    }

    @Singleton
    @Provides
    static DataManager providesDataManager(){
        return new DataManager();
    }

}
