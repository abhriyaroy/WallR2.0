package zebrostudio.wallr100.ui.collection;

import javax.inject.Inject;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.utils.FragmentTags;

/**
 * Listens to user actions from the UI ({@link CollectionFragment}), retrieves the data and updates
 * the UI as required.
 */
public class CollectionPresenterImpl implements CollectionFragmentContract.CollectionPresenter {

    private DataManager mDataManager;
    private CollectionFragmentContract.CollectionView mCollectionView;

    @Inject
    CollectionPresenterImpl(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void bindView(CollectionFragmentContract.CollectionView view) {
        mCollectionView = view;
    }

    @Override
    public void unbindView() {
        mCollectionView = null;
    }

    @Override
    public void updateCurrentFragmentTag() {
        mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.COLLECTIONS_FRAGMENT_TAG);
    }
}
