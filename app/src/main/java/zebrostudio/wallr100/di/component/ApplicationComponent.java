package zebrostudio.wallr100.di.component;

import javax.inject.Singleton;

import dagger.Component;
import zebrostudio.wallr100.WallRApplication;
import zebrostudio.wallr100.di.module.ApplicationModule;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(WallRApplication wallRApplication);

}
