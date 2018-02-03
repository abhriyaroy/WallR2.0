package zebrostudio.wallr100.di.component;

import dagger.Component;
import zebrostudio.wallr100.di.PerActivity;
import zebrostudio.wallr100.di.module.ActivityModule;
import zebrostudio.wallr100.ui.buy_pro.BuyProActivity;
import zebrostudio.wallr100.ui.collection.CollectionFragment;
import zebrostudio.wallr100.ui.explore.ExploreFragment;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.ui.minimal.MinimalFragment;
import zebrostudio.wallr100.ui.top_picks.TopPicksFragment;

@PerActivity
@Component(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);
    void inject(BuyProActivity buyProActivity);
    void inject(ExploreFragment exploreFragment);
    void inject(TopPicksFragment topPicksFragment);
    void inject(CollectionFragment collectionFragment);
    void inject(MinimalFragment minimalFragment);

}
