package zebrostudio.wallr100.ui.minimal;

import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface MinimalFragmentContract {

    interface MinimalView extends BaseView<MinimalPresenter> {

    }

    interface MinimalPresenter extends BasePresenter<MinimalView> {

        void updateCurrentFragmentTag();

    }
}
