package zebrostudio.wallr100.di.module;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.ui.main.MainActivityModule;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity bindMainActivity();

}