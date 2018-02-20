package zebrostudio.wallr100.ui.explore;

import android.content.Context;
import android.util.Log;

import javax.inject.Inject;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.di.ActivityScope;
import zebrostudio.wallr100.di.ApplicationContext;
import zebrostudio.wallr100.utils.FragmentTags;

@ActivityScope
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

    }

    @Override
    public void bindView(ExploreFragmentContract.ExploreView exploreView) {
        mExploreView = exploreView;
    }

    @Override
    public void unBindView() {
        mExploreView = null;
    }

    @Override
    public void updateCurrentFragmentTag() {
        mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.EXPLORE_FRAGMENT_TAG);
    }

}
