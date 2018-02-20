package zebrostudio.wallr100.ui.collection;

import javax.inject.Inject;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.utils.FragmentTags;

public class CollectionPresenterImpl implements CollectionFragmentContract.CollectionPresenter {

    DataManager mDataManager;

    @Inject
    CollectionPresenterImpl(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void bindView(CollectionFragmentContract.CollectionView view) {

    }

    @Override
    public void unbindView() {

    }

    @Override
    public void updateCurrentFragmentTag() {
        mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.COLLECTIONS_FRAGMENT_TAG);
    }
}
