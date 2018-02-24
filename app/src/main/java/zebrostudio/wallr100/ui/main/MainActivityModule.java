package zebrostudio.wallr100.ui.main;

import com.crashlytics.android.Crashlytics;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.di.FragmentScope;
import zebrostudio.wallr100.ui.base_fragment.BaseFragment;
import zebrostudio.wallr100.ui.base_fragment.BaseFragmentModule;
import zebrostudio.wallr100.ui.categories.CategoriesFragment;
import zebrostudio.wallr100.ui.categories.CategoriesModule;
import zebrostudio.wallr100.ui.collection.CollectionFragment;
import zebrostudio.wallr100.ui.collection.CollectionModule;
import zebrostudio.wallr100.ui.explore.ExploreFragment;
import zebrostudio.wallr100.ui.explore.ExploreModule;
import zebrostudio.wallr100.ui.minimal.MinimalFragment;
import zebrostudio.wallr100.ui.minimal.MinimalModule;
import zebrostudio.wallr100.ui.toppicks.TopPicksFragment;
import zebrostudio.wallr100.ui.toppicks.TopPicksModule;

@Module(includes = BaseFragmentModule.class)
public abstract class MainActivityModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = ExploreModule.class)
    abstract ExploreFragment exploreFragment();

    @FragmentScope
    @ContributesAndroidInjector(modules = TopPicksModule.class)
    abstract TopPicksFragment topPicksFragment();

    @FragmentScope
    @ContributesAndroidInjector(modules = CategoriesModule.class)
    abstract CategoriesFragment categoriesFragment();

    @FragmentScope
    @ContributesAndroidInjector(modules = MinimalModule.class)
    abstract MinimalFragment minimalFragment();

    @FragmentScope
    @ContributesAndroidInjector(modules = CollectionModule.class)
    abstract CollectionFragment collectionFragment();

    @FragmentScope
    @ContributesAndroidInjector(modules = BaseFragmentModule.class)
    abstract BaseFragment baseFragment();

    @Provides
    static MainActivityContract.Presenter providesMainActivityPresenter(DataManager dataManager) {
        return new MainActivityPresenterImpl(dataManager);
    }

    @Provides
    static Crashlytics providesCrashlytics() {
        return new Crashlytics();
    }

}
