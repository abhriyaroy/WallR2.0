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

    @Inject
    FragmentHandler() {

    }

    public void init() {
        mSupportFragmentManager = mMainActivity.getSupportFragmentManager();
        mFragmentContainer = R.id.home_container;
    }

    public void replaceContainerWithExploreFragment() {
        mSupportFragmentManager.beginTransaction()
                .replace(mFragmentContainer, mExploreFragment, FragmentTags.EXPLORE_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void replaceContainerWithTopPicksFragment() {
        mSupportFragmentManager.beginTransaction()
                .replace(mFragmentContainer, mTopPicksFragment, FragmentTags.TOP_PICKS_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void replaceContainerWithCategoriesFragment() {
        mSupportFragmentManager.beginTransaction()
                .replace(mFragmentContainer, mCategoriesFragment, FragmentTags.CATEGORIES_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void replaceContainerWithMinimalFragment() {
        mSupportFragmentManager.beginTransaction()
                .replace(mFragmentContainer, mMinimalFragment, FragmentTags.MINIMAL_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    public void replaceContainerWithCollectionsFragment() {
        mSupportFragmentManager.beginTransaction()
                .replace(mFragmentContainer, mCollectionsFragment, FragmentTags.COLLECTIONS_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }


}
