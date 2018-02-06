package zebrostudio.wallr100.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.crashlytics.android.Crashlytics;

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
import zebrostudio.wallr100.utils.GuillotineViewObject;
import zebrostudio.wallr100.utils.canaroTextViewUtils.CanaroTextView;

public class MainActivity extends DaggerAppCompatActivity implements MainActivityContract.View {

    private static boolean IS_OPEN = true;
    private Unbinder mUnBinder;

    @Inject
    Crashlytics crashlytics;
    @Inject
    GuillotineUtils mGuillotineUtils;
    @Inject
    GuillotineViewObject mGuillotineViewObject;
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
        initGuillotineViewObject();
        mGuillotineUtils.takeViewObject(mGuillotineViewObject);

        mFragmentHandler.init();
        mPresenter.bindView(this);
        mPresenter.requestExploreFragmentInflation();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.unbindView();
    }

    @Override
    public void onBackPressed() {
        if (mGuillotineUtils.getGuillotineState() == IS_OPEN) {
            mGuillotineUtils.closeguillotineMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setUpToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle(null);
        }
    }

    @Override
    public void setTitle(String title) {
        mToolbarTitle.setText(title);
    }

    @Override
    public void setTitlePadding(int left, int top, int right, int bottom) {
        mToolbarTitle.setPadding(left, top, right, bottom);
    }

    @Override
    public void initGuillotineViewObject() {
        mGuillotineViewObject.setmExploreLayout((LinearLayout) findViewById(R.id.explore_group));
        mGuillotineViewObject.setmExploreTitleView((CanaroTextView) findViewById(R.id.explore_textview));
        mGuillotineViewObject.setmTopPicksLayout((LinearLayout) findViewById(R.id.top_picks_group));
        mGuillotineViewObject.setmTopPicksTitleView((CanaroTextView) findViewById(R.id.top_picks_textview));
        mGuillotineViewObject.setmCategoriesLayout((LinearLayout) findViewById(R.id.categories_group));
        mGuillotineViewObject.setmCategoriesTitleView((CanaroTextView) findViewById(R.id.categories_textview));
        mGuillotineViewObject.setmMinimalLayout((LinearLayout) findViewById(R.id.minimal_group));
        mGuillotineViewObject.setmMinimalTitleView((CanaroTextView) findViewById(R.id.minimal_textview));
        mGuillotineViewObject.setmCollectionLayout((LinearLayout) findViewById(R.id.collection_group));
        mGuillotineViewObject.setmCollectionTitleView((CanaroTextView) findViewById(R.id.collection_textview));
        mGuillotineViewObject.setmFeedBackLayout((LinearLayout) findViewById(R.id.feedback_group));
        mGuillotineViewObject.setmBuyProLayout((LinearLayout) findViewById(R.id.buy_pro_group));
        mGuillotineViewObject.setmBuyProTitleView((CanaroTextView) findViewById(R.id.buy_pro_textview));
    }

    @Override
    public void configureNavigationBar() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.color_primary_dark));
        }
    }

    @Override
    public void closeGuillotineMenu() {
        mGuillotineUtils.closeguillotineMenu();
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
        mFragmentHandler.replaceWithExploreFragment();
    }

    @Override
    public void showTopPicksFragment() {
        mGuillotineUtils.highLightTopPicksGuillotineMenuItem();
        mFragmentHandler.replaceWithTopPicksFragment();
    }

    @Override
    public void showCategoriesFragment() {
        mGuillotineUtils.highLightCategoriesGuillotineMenuItem();
        mFragmentHandler.replaceWithCategoriesFragment();
    }

    @Override
    public void showMinimalFragment() {
        mGuillotineUtils.highLightMinimalGuillotineMenuItem();
        mFragmentHandler.replaceWithMinimalFragment();
    }

    @Override
    public void showCollectionsFragment() {
        mGuillotineUtils.highLightCollectionsGuillotineMenuItem();
        mFragmentHandler.replaceWithCollectionsFragment();
    }

    @Override
    public void showFeedBackTool() {
        mFeedback.getFeedback();
    }

    @Override
    public void showBuyProActivity() {

    }

    public void hideSearchOption() {
        mSearchIcon.setVisibility(View.GONE);
    }

    public void showSearchOption() {
        mSearchIcon.setVisibility(View.VISIBLE);
    }

    public void hideMultiSelectOption() {
        mMultiSelectIcon.setVisibility(View.GONE);
    }

    public void showMultiSelectOption() {
        mMultiSelectIcon.setVisibility(View.VISIBLE);
    }

}
