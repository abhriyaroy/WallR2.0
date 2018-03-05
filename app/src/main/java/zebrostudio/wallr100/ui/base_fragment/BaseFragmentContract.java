package zebrostudio.wallr100.ui.base_fragment;


import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface BaseFragmentContract {

    interface BaseFragmentView extends BaseView<BaseFragmentPresenter>{


    }

    interface BaseFragmentPresenter extends BasePresenter<BaseFragmentView>{

    }
}
