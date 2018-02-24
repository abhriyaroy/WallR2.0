package zebrostudio.wallr100.ui.explore;

import android.content.Context;

import javax.inject.Inject;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.di.ApplicationContext;
import zebrostudio.wallr100.di.FragmentScope;
import zebrostudio.wallr100.utils.FragmentTags;

/**
 * Listens to user actions from the UI ({@link ExploreFragment}), retrieves the data and updates
 * the UI as required.
 */
@FragmentScope
public class ExplorePresenterImpl implements ExploreFragmentContract.ExplorePresenter {

    private Context mContext;
    private DataManager mDataManager;
    private ExploreFragmentContract.ExploreView mExploreView;
    private boolean mConfirmExit;

    @Inject
    ExplorePresenterImpl(@ApplicationContext Context context,
                         DataManager dataManager){
        mContext = context;
        mDataManager = dataManager;
    }

    @Override
    public void unbindView() {
        mExploreView = null;
    }

    @Override
    public void bindView(ExploreFragmentContract.ExploreView exploreView) {
        mExploreView = exploreView;
    }

    @Override
    public void updateCurrentFragmentTag() {
        mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.EXPLORE_FRAGMENT_TAG);
    }

}
