package zebrostudio.wallr100.ui.categories;

import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

public interface CategoriesFragmentContract {

    interface CategoriesView extends BaseView<CategoriesPresenter>{

    }

    interface CategoriesPresenter extends BasePresenter<CategoriesView>{
        void updateCurrentFragmentTag();
    }
}
