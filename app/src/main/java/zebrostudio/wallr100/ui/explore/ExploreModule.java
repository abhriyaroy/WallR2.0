package zebrostudio.wallr100.ui.explore;


import android.content.Context;

import dagger.Module;
import dagger.Provides;
import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.di.ActivityScope;
import zebrostudio.wallr100.di.ApplicationContext;

@ActivityScope
@Module
public class ExploreModule {

    @Provides
    ExplorePresenterImpl providesExplorePresenterImpl(@ApplicationContext Context context,
                                                      DataManager dataManager) {
        return new ExplorePresenterImpl(context, dataManager);
    }
}
