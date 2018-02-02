package zebrostudio.wallr100.di.component;

import dagger.Component;
import zebrostudio.wallr100.di.PerActivity;
import zebrostudio.wallr100.di.module.ActivityModule;
import zebrostudio.wallr100.ui.BaseActivity;

@PerActivity
@Component(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(BaseActivity baseActivity);

}
