package zebrostudio.wallr100.ui.categories;

import javax.inject.Inject;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.utils.FragmentTags;

/**
 * Listens to user actions from the UI ({@link CategoriesFragment}), retrieves the data and updates
 * the UI as required.
 */
public class CategoriesPresenterImpl implements CategoriesFragmentContract.CategoriesPresenter {

    private DataManager mDataManager;
    private CategoriesFragmentContract.CategoriesView mCategoriesView;

    @Inject
    CategoriesPresenterImpl(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void bindView(CategoriesFragmentContract.CategoriesView view) {
        mCategoriesView = view;
    }

    @Override
    public void unbindView() {
        mCategoriesView = null;
    }

    @Override
    public void updateCurrentFragmentTag() {
        mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.CATEGORIES_FRAGMENT_TAG);
    }
}
