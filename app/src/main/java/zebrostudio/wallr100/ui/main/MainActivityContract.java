package zebrostudio.wallr100.ui.main;

import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

public interface MainActivityContract {

    interface View extends BaseView<Presenter>{

    }

    interface Presenter extends BasePresenter<View>{

    }
}
