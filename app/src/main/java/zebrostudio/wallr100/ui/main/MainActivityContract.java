package zebrostudio.wallr100.ui.main;

import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

public interface MainActivityContract {

    interface View extends BaseView<Presenter> {

        void setUpToolbar();

        void setTitle(String title);

        void setTitlePadding(int left, int top, int right, int bottom);

        void configureNavigationBar();

        void initGuillotineViewObject();

        void closeGuillotineMenu();

        void hideBuyProGuillotineMenuItem();

        void showBuyProGuillotineMenuItem();

        void hideProBadge();

        void showProBadge();

        void exploreGuillotineMenuItemClicked();

        void topPicksGuillotineMenuItemClicked();

        void categoriesGuillotineMenuItemClicked();

        void minimalGuillotineMenuItemClicked();

        void collectionsGuillotineMenuItemClicked();

        void feedbackGuillotineMenuItemClicked();

        void buyProGuillotineMenuItemClicked();

        void showExploreFragment();

        void showTopPicksFragment();

        void showCategoriesFragment();

        void showMinimalFragment();

        void showCollectionsFragment();

        void showFeedBackTool();

        void showBuyProActivity();
    }

    interface Presenter extends BasePresenter<View> {

        void requestExploreFragmentInflation();

        void requestTopPicksFragmentInflation();

        void requestCategoriesFragmentInflation();

        void requestMinimalFragmentInflation();

        void requestCollectionsFragmentInflation();

        void requestFeedbackTool();

        void requestBuyProActivity();

        void checkIfProUser();
    }
}
