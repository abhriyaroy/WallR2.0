package zebrostudio.wallr100.ui.main;

import android.os.Handler;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import io.fabric.sdk.android.Fabric;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.ui.categories.CategoriesFragment;
import zebrostudio.wallr100.ui.collection.CollectionFragment;
import zebrostudio.wallr100.ui.explore.ExploreFragment;
import zebrostudio.wallr100.ui.feedback.Feedback;
import zebrostudio.wallr100.ui.minimal.MinimalFragment;
import zebrostudio.wallr100.ui.toppicks.TopPicksFragment;
import zebrostudio.wallr100.utils.FragmentTags;
import zebrostudio.wallr100.utils.Toasty;
import zebrostudio.wallr100.utils.canarotextviewutils.CanaroTextView;

/**
 *  Loads in when the app is opened. It is responsible for handling the various fragments which
 *  represent the various groups of wallpapers. This is also responsible for handling the
 *  Guillotine Menu which is a customized replacement to the traditional navigation drawer.
 */
public class MainActivity extends DaggerAppCompatActivity implements MainActivityContract.View {

    private static final boolean IS_OPEN = true;
    private static final long RIPPLE_DURATION = 250;

    private Unbinder mUnBinder;
    private int mFragmentContainer = R.id.home_container;
    private View mGuillotineMenu;
    private GuillotineListener mGuillotineListener;
    private boolean mIsGuillotineOpened;
    private GuillotineAnimation mGuillotineAnimationBuilder;

    @Inject
    Crashlytics crashlytics;
    @Inject
    MainActivityContract.Presenter mPresenter;
    @Inject
    Feedback mFeedback;
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

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;
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
    @BindView(R.id.explore_group)
    LinearLayout mExploreLayout;
    @BindView(R.id.top_picks_group)
    LinearLayout mTopPicksLayout;
    @BindView(R.id.categories_group)
    LinearLayout mCategoriesLayout;
    @BindView(R.id.minimal_group)
    LinearLayout mMinimalLayout;
    @BindView(R.id.collection_group)
    LinearLayout mCollectionLayout;
    @BindView(R.id.feedback_group)
    LinearLayout mFeedBackLayout;
    @BindView(R.id.buy_pro_group)
    LinearLayout mBuyProLayout;
    @BindView(R.id.explore_textview)
    CanaroTextView mExploreGuillotineTitle;
    @BindView(R.id.top_picks_textview)
    CanaroTextView mTopPicksGuillotineTitle;
    @BindView(R.id.categories_textview)
    CanaroTextView mCategoriesGuillotineTitle;
    @BindView(R.id.minimal_textview)
    CanaroTextView mMinimalGuillotineTitle;
    @BindView(R.id.collection_textview)
    CanaroTextView mCollectionGuillotineTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidInjection.inject(this);
        mGuillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine_layout, null);
        mRootView = findViewById(R.id.root_layout);
        mRootView.addView(mGuillotineMenu);

        mUnBinder = ButterKnife.bind(this);
        Fabric.with(this, crashlytics);

        setUpToolbar();
        configureNavigationBar();

        setGuillotineListener();
        guillotineAnimationBuilder();

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
        mUnBinder.unbind();
        mPresenter.unbindView();
    }

    @Override
    public void onBackPressed() {
        if (getGuillotineState() == IS_OPEN) {
            closeGuillotineMenu();
        } else {
            if (!mPresenter.handleBackPress()) {
                super.onBackPressed();
            }
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
    public boolean getGuillotineState() {
        return mIsGuillotineOpened;
    }

    @Override
    public void closeGuillotineMenu() {
        mGuillotineAnimationBuilder.close();
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
    public void showExploreFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(mFragmentContainer, mExploreFragment, FragmentTags.EXPLORE_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showTopPicksFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(mFragmentContainer, mTopPicksFragment, FragmentTags.TOP_PICKS_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showCategoriesFragment() {
        resetAllMenuItemHighlighting();
        mCategoriesGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
        getSupportFragmentManager().beginTransaction()
                .replace(mFragmentContainer, mCategoriesFragment, FragmentTags.CATEGORIES_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showMinimalFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(mFragmentContainer, mMinimalFragment, FragmentTags.MINIMAL_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showCollectionsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(mFragmentContainer, mCollectionsFragment, FragmentTags.COLLECTIONS_FRAGMENT_TAG)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void showFeedBackTool() {
        mFeedback.getFeedback();
    }

    @Override
    public void showBuyProActivity() {

    }

    @Override
    public void showAppExitConfirmationToast() {
        try {
            Toasty.info(this, "Press back again to exit",
                    Toast.LENGTH_SHORT, true).show();
        } catch (NullPointerException e) {

        }
    }

    @Override
    public void runTimeoutChecker() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mPresenter.resetExitConfirmation();
            }
        }, 2000);
    }

    @Override
    public void exitFromApp() {
        this.finish();
    }

    @OnClick(R.id.explore_group)
    public void exploreGuillotineMenuItemClicked() {
        mPresenter.requestExploreFragmentInflation();
    }

    @OnClick(R.id.top_picks_group)
    public void topPicksGuillotineMenuItemClicked() {
        mPresenter.requestTopPicksFragmentInflation();
    }

    @OnClick(R.id.categories_group)
    public void categoriesGuillotineMenuItemClicked() {
        mPresenter.requestCategoriesFragmentInflation();
    }

    @OnClick(R.id.minimal_group)
    public void minimalGuillotineMenuItemClicked() {
        mPresenter.requestMinimalFragmentInflation();
    }

    @OnClick(R.id.collection_group)
    public void collectionsGuillotineMenuItemClicked() {
        mPresenter.requestCollectionsFragmentInflation();
    }

    @OnClick(R.id.feedback_group)
    public void feedbackGuillotineMenuItemClicked() {
        mPresenter.requestFeedbackTool();
    }

    @OnClick(R.id.buy_pro_group)
    public void buyProGuillotineMenuItemClicked() {
        mPresenter.requestBuyProActivity();
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

    private void setGuillotineListener() {
        mGuillotineListener = new GuillotineListener() {
            @Override
            public void onGuillotineOpened() {
                Log.d("Mainactivity","guillotine is open");
                mIsGuillotineOpened = true;
            }

            @Override
            public void onGuillotineClosed() {
                Log.d("Mainactivity","guillotine is closed");
                mIsGuillotineOpened = false;
            }
        };
    }

    private void guillotineAnimationBuilder() {
        mGuillotineAnimationBuilder = new GuillotineAnimation.GuillotineBuilder(
                mGuillotineMenu, mGuillotineMenu.findViewById(R.id.guillotine_hamburger), mHamburgerIcon)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(mToolbar)
                .setGuillotineListener(mGuillotineListener)
                .setClosedOnStart(true)
                .build();
    }

    private void hideBuyProTitle() {
        mBuyProLayout.setVisibility(View.GONE);
        mBuyProLayout.setClickable(false);
    }

    private void showBuyProTitle() {
        mBuyProLayout.setVisibility(View.VISIBLE);
        mBuyProLayout.setClickable(true);
    }

    public void resetAllMenuItemHighlighting() {
        mExploreGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mTopPicksGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCategoriesGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mMinimalGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCollectionGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
    }

    public void highlightExploreMenu() {
        resetAllMenuItemHighlighting();
        mExploreGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
    }

    public void highlightTopPicksMenu() {
        resetAllMenuItemHighlighting();
        mTopPicksGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
    }

    public void highlightCategoriesMenu() {
        resetAllMenuItemHighlighting();
        mCategoriesGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
    }

    public void highlightMinimalMenu() {
        resetAllMenuItemHighlighting();
        mMinimalGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
    }

    public void highlightCollectionsMenu() {
        resetAllMenuItemHighlighting();
        mCollectionGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
    }
}
