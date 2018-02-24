package zebrostudio.wallr100.ui.minimal;

import javax.inject.Inject;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.utils.FragmentTags;

/**
 * Listens to user actions from the UI ({@link MinimalFragment}), retrieves the data and updates
 * the UI as required.
 */
public class MinimalPresenterImpl implements MinimalFragmentContract.MinimalPresenter {

    private DataManager mDataManager;
    private MinimalFragmentContract.MinimalView mMinimalView;

    @Inject
    MinimalPresenterImpl(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void bindView(MinimalFragmentContract.MinimalView view) {
        mMinimalView = view;
    }

    @Override
    public void unbindView() {
        mMinimalView = null;
    }

    @Override
    public void updateCurrentFragmentTag() {
        mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.MINIMAL_FRAGMENT_TAG);
    }
}
