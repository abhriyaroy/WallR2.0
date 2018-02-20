package zebrostudio.wallr100.ui.minimal;

import javax.inject.Inject;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.utils.FragmentTags;

public class MinimalPresenterImpl implements MinimalFragmentContract.MinimalPresenter {

    DataManager mDataManager;

    @Inject
    MinimalPresenterImpl(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void bindView(MinimalFragmentContract.MinimalView view) {

    }

    @Override
    public void unbindView() {

    }

    @Override
    public void updateCurrentFragmentTag() {
        mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.MINIMAL_FRAGMENT_TAG);
    }
}
