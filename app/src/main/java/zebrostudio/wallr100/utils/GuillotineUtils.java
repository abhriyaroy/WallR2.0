package zebrostudio.wallr100.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import zebrostudio.wallr100.R;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.utils.canaroTextViewUtils.CanaroTextView;

public class GuillotineUtils {

    private static final long RIPPLE_DURATION = 250;

    private MainActivity mActivity;
    private Toolbar mToolbar;
    private View mGuillotineMenu;
    private Unbinder mUnbinder;
    private GuillotineListener mGuillotineListener;
    private boolean mIsGuillotineOpened;
    private GuillotineAnimation mGuillotineAnimationBuilder;
    private View mHamburgerIcon;
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

    @Inject
    public GuillotineUtils(MainActivity activity) {
        mActivity = activity;
    }

    public void init(Context context, FrameLayout rootView, Toolbar toolbar, View hamburgerIcon) {
        inflateAndAddGuillotineMenu(context, rootView);
        setHamburgerIcon(hamburgerIcon);
        setToolbar(toolbar);
        setGuillotineListener();
        guillotineAnimationBuilder();
        setMenuItemsClickListener();
    }

    private void setToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
    }

    private void inflateAndAddGuillotineMenu(Context context, FrameLayout rootView) {
        mGuillotineMenu = LayoutInflater.from(context).inflate(R.layout.guillotine_layout, null);
        mUnbinder = ButterKnife.bind(this, mGuillotineMenu);
        rootView.addView(mGuillotineMenu);
    }

    private void setHamburgerIcon(View hamburgerIcon) {
        mHamburgerIcon = hamburgerIcon;
    }

    private void setGuillotineListener() {
        mGuillotineListener = new GuillotineListener() {
            @Override
            public void onGuillotineOpened() {
                mIsGuillotineOpened = true;
            }

            @Override
            public void onGuillotineClosed() {
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

    private void hideBuyProTitle(){
        mBuyProLayout.setVisibility(View.GONE);
        mBuyProLayout.setClickable(false);
    }

    private void showBuyProTitle(){
        mBuyProLayout.setVisibility(View.VISIBLE);
        mBuyProLayout.setClickable(true);
    }

    private void hideProBadge(){

    }

    private void showProBadge(){

    }

    private void setMenuItemsClickListener() {
        mExploreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.exploreGuillotineMenuItemClicked();
            }
        });

        mTopPicksLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.topPicksGuillotineMenuItemClicked();
            }
        });

        mCategoriesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.categoriesGuillotineMenuItemClicked();
            }
        });

        mMinimalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.minimalGuillotineMenuItemClicked();
            }
        });

        mCollectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.collectionsGuillotineMenuItemClicked();
            }
        });

        mFeedBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.feedbackGuillotineMenuItemClicked();
            }
        });

        mBuyProLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.buyProGuillotineMenuItemClicked();
            }
        });
    }

    public void resetAllMenuItemHighlighting(){
        mExploreGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mTopPicksGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCategoriesGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mMinimalGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCollectionGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
    }

    public void highLightExploreGuillotineMenuItem(){
        resetAllMenuItemHighlighting();
        mExploreGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));

    }

    public void highLightTopPicksGuillotineMenuItem(){
        resetAllMenuItemHighlighting();
        mTopPicksGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
    }

    public void highLightCategoriesGuillotineMenuItem(){
        resetAllMenuItemHighlighting();
        mCategoriesGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
    }

    public void highLightMinimalGuillotineMenuItem(){
        resetAllMenuItemHighlighting();
        mMinimalGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
    }

    public void highLightCollectionsGuillotineMenuItem(){
        resetAllMenuItemHighlighting();
        mCollectionGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
    }

    public boolean getGuillotineState() {
        return mIsGuillotineOpened;
    }

    public void closeGuillotineMenu() {
        mGuillotineAnimationBuilder.close();
    }

}
