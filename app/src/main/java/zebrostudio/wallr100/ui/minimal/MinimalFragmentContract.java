package zebrostudio.wallr100.ui.minimal;

import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

public interface MinimalFragmentContract {

    interface MinimalView extends BaseView<MinimalPresenter> {

    }

    interface MinimalPresenter extends BasePresenter<MinimalView> {
        void updateCurrentFragmentTag();
    }
}
