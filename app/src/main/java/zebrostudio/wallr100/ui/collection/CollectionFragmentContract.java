package zebrostudio.wallr100.ui.collection;

import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

public interface CollectionFragmentContract {

    interface CollectionView extends BaseView<CollectionPresenter> {

    }

    interface CollectionPresenter extends BasePresenter<CollectionView> {

        void updateCurrentFragmentTag();

    }
}
