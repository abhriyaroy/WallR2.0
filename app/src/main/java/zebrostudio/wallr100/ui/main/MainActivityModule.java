package zebrostudio.wallr100.ui.main;

import com.crashlytics.android.Crashlytics;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.di.module.FragmentScope;
import zebrostudio.wallr100.ui.categories.CategoriesFragment;
import zebrostudio.wallr100.ui.collection.CollectionFragment;
import zebrostudio.wallr100.ui.explore.ExploreFragment;
import zebrostudio.wallr100.ui.minimal.MinimalFragment;
import zebrostudio.wallr100.ui.top_picks.TopPicksFragment;

@Module
public abstract class MainActivityModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract ExploreFragment exploreFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract TopPicksFragment topPicksFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract CategoriesFragment categoriesFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract MinimalFragment minimalFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract CollectionFragment collectionFragment();

    @Provides
    static MainActivityContract.Presenter providesMainActivityPresenter(DataManager dataManager) {
        return new MainActivityPresenterImpl(dataManager);
    }

    @Provides
    static Crashlytics providesCrashlytics() {
        return new Crashlytics();
    }

}
