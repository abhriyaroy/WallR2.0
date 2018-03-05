package zebrostudio.wallr100.ui.main;

import android.content.pm.PackageManager;

import zebrostudio.wallr100.BasePresenter;
import zebrostudio.wallr100.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface MainActivityContract {

    interface View extends BaseView<Presenter> {

        void setTitlePadding(int left, int top, int right, int bottom);

        void configureNavigationBar();

        boolean getGuillotineState();

        void closeGuillotineMenu();

        void hideBuyProGuillotineMenuItem();

        void showBuyProGuillotineMenuItem();

        void hideProBadge();

        void showProBadge();

        void showExploreFragment();

        void showTopPicksFragment();

        void showCategoriesFragment();

        void showMinimalFragment();

        void showCollectionsFragment();

        void showFeedBackTool();

        void showAppExitConfirmationToast();

        void runTimeoutChecker();

        void exitFromApp();

        PackageManager getAppPackageManager();
    }

    interface Presenter extends BasePresenter<View> {

        void requestExploreFragmentInflation();

        void requestTopPicksFragmentInflation();

        void requestCategoriesFragmentInflation();

        void requestMinimalFragmentInflation();

        void requestCollectionsFragmentInflation();

        void requestFeedbackTool();

        void checkIfProUser();

        boolean handleBackPress();

        void resetExitConfirmation();
    }
}
