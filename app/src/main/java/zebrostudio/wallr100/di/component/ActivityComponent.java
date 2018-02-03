package zebrostudio.wallr100.di.component;

import dagger.Component;
import zebrostudio.wallr100.di.PerActivity;
import zebrostudio.wallr100.di.module.ActivityModule;
import zebrostudio.wallr100.di.module.MainActivityModule;
import zebrostudio.wallr100.ui.MainActivity;

@PerActivity
@Component(modules = {ActivityModule.class, MainActivityModule.class})
public interface ActivityComponent {

    void inject(MainActivity mainActivity);

}
