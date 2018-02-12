package zebrostudio.wallr100.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerAppCompatActivity;
import io.fabric.sdk.android.Fabric;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.ui.feedback.Feedback;
import zebrostudio.wallr100.utils.FragmentHandler;
import zebrostudio.wallr100.utils.GuillotineUtils;
import zebrostudio.wallr100.utils.canaroTextViewUtils.CanaroTextView;

public class MainActivity extends DaggerAppCompatActivity implements MainActivityContract.View {

    private static boolean IS_OPEN = true;
    private Unbinder mUnBinder;

    @Inject
    Crashlytics crashlytics;
    @Inject
    GuillotineUtils mGuillotineUtils;
    @Inject
    MainActivityContract.Presenter mPresenter;
    @Inject
    FragmentHandler mFragmentHandler;
    @Inject
    Feedback mFeedback;

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;
    @BindView(R.id.root_layout)
    public FrameLayout mRootView;
    @BindView(R.id.content_hamburger)
    public View mHamburgerIcon;
    @BindView(R.id.toolbar_title)
    public CanaroTextView mToolbarTitle;
    @BindView(R.id.toolbar_search_icon)
    public ImageView mSearchIcon;
    @BindView(R.id.toolbar_multi_select_icon)
    public ImageView mMultiSelectIcon;
    @BindView(R.id.minimal_fragment_bottom_layout)
    public RelativeLayout mMinimalFragmentBottomLayout;
    @BindView(R.id.tab_layout)
    public SmartTabLayout mSmartTabLayout;
    @BindView(R.id.collection_switch_layout)
    public RelativeLayout mCollectionSwitchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUnBinder = ButterKnife.bind(this);
        Fabric.with(this, crashlytics);

        setUpToolbar();
        configureNavigationBar();

        mGuillotineUtils.init(this,
                mRootView,
                mToolbar,
                mHamburgerIcon);

        mFragmentHandler.init();
        mPresenter.bindView(this);
        mPresenter.requestExploreFragmentInflation();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unbindView();
    }

    @Override
    public void onBackPressed() {
        if (mGuillotineUtils.getGuillotineState() == IS_OPEN) {
            mGuillotineUtils.closeGuillotineMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setTitlePadding(int left, int top, int right, int bottom) {
        mToolbarTitle.setPadding(left, top, right, bottom);
    }

    @Override
    public void configureNavigationBar() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_primary_dark));
        }
    }

    @Override
    public void closeGuillotineMenu() {
        mGuillotineUtils.closeGuillotineMenu();
    }

    @Override
    public void hideBuyProGuillotineMenuItem() {

    }

    @Override
    public void showBuyProGuillotineMenuItem() {

    }

    @Override
    public void hideProBadge() {

    }

    @Override
    public void showProBadge() {

    }

    @Override
    public void exploreGuillotineMenuItemClicked() {
        mPresenter.requestExploreFragmentInflation();
    }

    @Override
    public void topPicksGuillotineMenuItemClicked() {
        mPresenter.requestTopPicksFragmentInflation();
    }

    @Override
    public void categoriesGuillotineMenuItemClicked() {
        mPresenter.requestCategoriesFragmentInflation();
    }

    @Override
    public void minimalGuillotineMenuItemClicked() {
        mPresenter.requestMinimalFragmentInflation();
    }

    @Override
    public void collectionsGuillotineMenuItemClicked() {
        mPresenter.requestCollectionsFragmentInflation();
    }

    @Override
    public void feedbackGuillotineMenuItemClicked() {
        mPresenter.requestFeedbackTool();
    }

    @Override
    public void buyProGuillotineMenuItemClicked() {
        mPresenter.requestBuyProActivity();
    }

    @Override
    public void showExploreFragment() {
        mGuillotineUtils.highLightExploreGuillotineMenuItem();
        mFragmentHandler.replaceContainerWithExploreFragment();
    }

    @Override
    public void showTopPicksFragment() {
        mGuillotineUtils.highLightTopPicksGuillotineMenuItem();
        mFragmentHandler.replaceContainerWithTopPicksFragment();
    }

    @Override
    public void showCategoriesFragment() {
        mGuillotineUtils.highLightCategoriesGuillotineMenuItem();
        mFragmentHandler.replaceContainerWithCategoriesFragment();
    }

    @Override
    public void showMinimalFragment() {
        mGuillotineUtils.highLightMinimalGuillotineMenuItem();
        mFragmentHandler.replaceContainerWithMinimalFragment();
    }

    @Override
    public void showCollectionsFragment() {
        mGuillotineUtils.highLightCollectionsGuillotineMenuItem();
        mFragmentHandler.replaceContainerWithCollectionsFragment();
    }

    @Override
    public void showFeedBackTool() {
        mFeedback.getFeedback();
    }

    @Override
    public void showBuyProActivity() {

    }

    public void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(null);
        }
    }

    public void setTitle(String title) {
        mToolbarTitle.setText(title);
    }

}
