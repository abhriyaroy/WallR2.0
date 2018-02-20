package zebrostudio.wallr100.ui.categories;

import javax.inject.Inject;

import zebrostudio.wallr100.data.DataManager;
import zebrostudio.wallr100.utils.FragmentTags;

public class CategoriesPresenterImpl implements CategoriesFragmentContract.CategoriesPresenter {

    DataManager mDataManager;

    @Inject
    CategoriesPresenterImpl(DataManager dataManager){
        mDataManager = dataManager;
    }

    @Override
    public void bindView(CategoriesFragmentContract.CategoriesView view) {

    }

    @Override
    public void unbindView() {

    }

    @Override
    public void updateCurrentFragmentTag() {
        mDataManager.setCurrentlyInflatedFragmentTag(FragmentTags.CATEGORIES_FRAGMENT_TAG);
    }
}
