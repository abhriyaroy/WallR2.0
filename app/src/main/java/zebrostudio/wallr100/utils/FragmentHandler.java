package zebrostudio.wallr100.utils;

import android.support.v4.app.FragmentManager;

import javax.inject.Inject;

import zebrostudio.wallr100.R;
import zebrostudio.wallr100.ui.categories.CategoriesFragment;
import zebrostudio.wallr100.ui.collection.CollectionFragment;
import zebrostudio.wallr100.ui.explore.ExploreFragment;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.ui.minimal.MinimalFragment;
import zebrostudio.wallr100.ui.top_picks.TopPicksFragment;

public class FragmentHandler {

    @Inject
    FragmentHandler() {

    }

    @Inject
    MainActivity mMainActivity;
    @Inject
    ExploreFragment mExploreFragment;
    @Inject
    TopPicksFragment mTopPicksFragment;
    @Inject
    CategoriesFragment mCategoriesFragment;
    @Inject
    MinimalFragment mMinimalFragment;
    @Inject
    CollectionFragment mCollectionsFragment;

    private FragmentManager mSupportFragmentManager;
    private int mFragmentContainer;

    public void init() {
        mSupportFragmentManager = mMainActivity.getSupportFragmentManager();
        mFragmentContainer = R.id.home_container;
    }

    public void replaceWithExploreFragment() {
        mSupportFragmentManager.beginTransaction()
                .replace(R.id.home_container, mExploreFragment, FragmentTags.EXPLORE_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void replaceWithTopPicksFragment() {
        mSupportFragmentManager.beginTransaction()
                .replace(R.id.home_container, mTopPicksFragment, FragmentTags.TOP_PICKS_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void replaceWithCategoriesFragment() {
        mSupportFragmentManager.beginTransaction()
                .replace(R.id.home_container, mCategoriesFragment, FragmentTags.CATEGORIES_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void replaceWithMinimalFragment() {
        mSupportFragmentManager.beginTransaction()
                .replace(R.id.home_container, mMinimalFragment, FragmentTags.MINIMAL_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void replaceWithCollectionsFragment() {
        mSupportFragmentManager.beginTransaction()
                .replace(R.id.home_container, mCollectionsFragment, FragmentTags.COLLECTIONS_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }


}
