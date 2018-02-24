package zebrostudio.wallr100.ui.toppicks;

import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface TopPicksFragmentContract {

    interface TopPicksView extends BaseView<TopPicksPresenter> {

    }

    interface TopPicksPresenter extends BasePresenter<TopPicksView> {

        void updateCurrentFragmentTag();

    }
}
