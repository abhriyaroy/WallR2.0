package zebrostudio.wallr100.ui.base_fragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import zebrostudio.wallr100.di.FragmentScope;
import zebrostudio.wallr100.ui.categories.buildings.BuildingWallpapersFragment;
import zebrostudio.wallr100.ui.categories.food.FoodWallpapersFragment;
import zebrostudio.wallr100.ui.categories.nature.NatureWallpapersFragment;
import zebrostudio.wallr100.ui.categories.objects.ObjectWallpapersFragment;
import zebrostudio.wallr100.ui.categories.people.PeopleWallpapersFragment;
import zebrostudio.wallr100.ui.categories.technology.TechnologyWallpapersFragment;
import zebrostudio.wallr100.ui.toppicks.popular.PopularWallpapersFragment;
import zebrostudio.wallr100.ui.toppicks.recents.RecentWallpapersFragment;
import zebrostudio.wallr100.ui.toppicks.standouts.StandoutWallpaperFragment;

@Module
public abstract class BaseFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract RecentWallpapersFragment recentWallpapersFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract PopularWallpapersFragment popularWallpapersFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract StandoutWallpaperFragment standoutWallpaperFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract BuildingWallpapersFragment buildingWallpapersFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract FoodWallpapersFragment foodWallpapersFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract NatureWallpapersFragment natureWallpapersFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract ObjectWallpapersFragment objectWallpapersFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract PeopleWallpapersFragment peopleWallpapersFragment();

    @FragmentScope
    @ContributesAndroidInjector
    abstract TechnologyWallpapersFragment technologyWallpapersFragment();

}
