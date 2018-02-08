package zebrostudio.wallr100.ui.main;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.utils.FragmentTags;

public class MainActivityPresenterImpl implements MainActivityContract.Presenter {

    DataManager mDataManager;

    private MainActivityContract.View mMainView;

    MainActivityPresenterImpl(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void bindView(MainActivityContract.View view) {
        mMainView = view;
    }

    @Override
    public void unbindView() {
        mMainView = null;
    }

    @Override
    public void requestExploreFragmentInflation() {
        if (!mDataManager.getCurrentlyInflatedFragmentTag()
                .equalsIgnoreCase(FragmentTags.EXPLORE_FRAGMENT_TAG)) {
            mMainView.showExploreFragment();
            mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.EXPLORE_FRAGMENT_TAG);
        }
        mMainView.closeGuillotineMenu();
    }

    @Override
    public void requestTopPicksFragmentInflation() {
        if (!mDataManager.getCurrentlyInflatedFragmentTag()
                .equalsIgnoreCase(FragmentTags.TOP_PICKS_FRAGMENT_TAG)) {
            mMainView.showTopPicksFragment();
            mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.TOP_PICKS_FRAGMENT_TAG);
        }
        mMainView.closeGuillotineMenu();
    }

    @Override
    public void requestCategoriesFragmentInflation() {
        if (!mDataManager.getCurrentlyInflatedFragmentTag()
                .equalsIgnoreCase(FragmentTags.CATEGORIES_FRAGMENT_TAG)) {
            mMainView.showCategoriesFragment();
            mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.CATEGORIES_FRAGMENT_TAG);
        }
        mMainView.closeGuillotineMenu();
    }

    @Override
    public void requestMinimalFragmentInflation() {
        if (!mDataManager.getCurrentlyInflatedFragmentTag()
                .equalsIgnoreCase(FragmentTags.MINIMAL_FRAGMENT_TAG)) {
            mMainView.showMinimalFragment();
            mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.MINIMAL_FRAGMENT_TAG);
        }
        mMainView.closeGuillotineMenu();
    }

    @Override
    public void requestCollectionsFragmentInflation() {
        if (!mDataManager.getCurrentlyInflatedFragmentTag()
                .equalsIgnoreCase(FragmentTags.COLLECTIONS_FRAGMENT_TAG)) {
            mMainView.showCollectionsFragment();
            mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.COLLECTIONS_FRAGMENT_TAG);
        }
        mMainView.closeGuillotineMenu();
    }

    @Override
    public void requestFeedbackTool() {
        mMainView.showFeedBackTool();
        mMainView.closeGuillotineMenu();
    }

    @Override
    public void requestBuyProActivity() {

    }

    @Override
    public void checkIfProUser() {
        if (mDataManager.checkIfProLocal()){
            mMainView.hideBuyProGuillotineMenuItem();
            mMainView.showProBadge();
        }
        else {
        }
    }
}
