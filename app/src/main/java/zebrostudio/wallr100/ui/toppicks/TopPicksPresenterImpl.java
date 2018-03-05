package zebrostudio.wallr100.ui.toppicks;

import javax.inject.Inject;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.utils.FragmentTags;

/**
 * Listens to user actions from the UI ({@link TopPicksFragment}), retrieves the data and updates
 * the UI as required.
 */
public class TopPicksPresenterImpl implements TopPicksFragmentContract.TopPicksPresenter {

    private DataManager mDataManager;
    private TopPicksFragmentContract.TopPicksView mTopPicksView;

    @Inject
    public TopPicksPresenterImpl(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void bindView(TopPicksFragmentContract.TopPicksView view) {
        mTopPicksView = view;
    }

    @Override
    public void unbindView() {
        mTopPicksView = null;
    }

    @Override
    public void updateCurrentFragmentTag() {
        mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.MINIMAL_FRAGMENT_TAG);
    }
}
