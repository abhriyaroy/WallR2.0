package zebrostudio.wallr100.di.module;

import dagger.Module;
import dagger.Provides;
import zebrostudio.wallr100.ui.MainActivity;

@Module
public class MainActivityModule {

    private final MainActivity mActivity;

    public MainActivityModule(MainActivity mainActivity){
        mActivity = mainActivity;
    }

    @Provides
    public MainActivity providesMainActivity(){
        return mActivity;
    }

}
