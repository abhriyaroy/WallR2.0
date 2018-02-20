package zebrostudio.wallr100.ui.base_fragment;


import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

public interface BaseFragmentContract {

    interface BaseFragmentView extends BaseView<BaseFragmentPresenter>{


    }

    interface BaseFragmentPresenter extends BasePresenter<BaseFragmentView>{

    }
}
