package zebrostudio.wallr100.ui.main;

import javax.inject.Inject;

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
        if (!mDataManager.getCurrentlyInflatedFragemntTag()
                .equalsIgnoreCase(FragmentTags.EXPLORE_FRAGMENT_TAG)) {
            mMainView.showExploreFragment();
            mDataManager.setCurrentlyInflatedFragemntTag(FragmentTags.EXPLORE_FRAGMENT_TAG);
        }
        mMainView.closeGuillotineMenu();
    }

    @Override
    public void requestTopPicksFragmentInflation() {
        if (!mDataManager.getCurrentlyInflatedFragemntTag()
                .equalsIgnoreCase(FragmentTags.TOP_PICKS_FRAGMENT_TAG)) {
            mMainView.showTopPicksFragment();
            mDataManager.setCurrentlyInflatedFragemntTag(FragmentTags.TOP_PICKS_FRAGMENT_TAG);
        }
        mMainView.closeGuillotineMenu();
    }

    @Override
    public void requestCategoriesFragmentInflation() {
        if (!mDataManager.getCurrentlyInflatedFragemntTag()
                .equalsIgnoreCase(FragmentTags.CATEGORIES_FRAGMENT_TAG)) {
            mMainView.showCategoriesFragment();
            mDataManager.setCurrentlyInflatedFragemntTag(FragmentTags.CATEGORIES_FRAGMENT_TAG);
        }
        mMainView.closeGuillotineMenu();
    }

    @Override
    public void requestMinimalFragmentInflation() {
        if (!mDataManager.getCurrentlyInflatedFragemntTag()
                .equalsIgnoreCase(FragmentTags.MINIMAL_FRAGMENT_TAG)) {
            mMainView.showMinimalFragment();
            mDataManager.setCurrentlyInflatedFragemntTag(FragmentTags.MINIMAL_FRAGMENT_TAG);
        }
        mMainView.closeGuillotineMenu();
    }

    @Override
    public void requestCollectionsFragmentInflation() {
        if (!mDataManager.getCurrentlyInflatedFragemntTag()
                .equalsIgnoreCase(FragmentTags.COLLECTIONS_FRAGMENT_TAG)) {
            mMainView.showCollectionsFragment();
            mDataManager.setCurrentlyInflatedFragemntTag(FragmentTags.COLLECTIONS_FRAGMENT_TAG);
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
}
