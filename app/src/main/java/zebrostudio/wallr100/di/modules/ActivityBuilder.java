package zebrostudio.wallr100.di.modules;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import zebrostudio.wallr100.ui.buypro.BuyProActivity;
import zebrostudio.wallr100.ui.buypro.BuyProActivityModule;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.ui.main.MainActivityModule;
import zebrostudio.wallr100.ui.search.SearchActivity;
import zebrostudio.wallr100.ui.search.SearchActivityModule;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector(modules = SearchActivityModule.class)
    abstract SearchActivity bindSearchActivity();

    @ContributesAndroidInjector(modules = BuyProActivityModule.class)
    abstract BuyProActivity bindBuyProActivity();

}