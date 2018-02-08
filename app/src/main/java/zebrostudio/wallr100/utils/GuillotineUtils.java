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

import zebrostudio.wallr100.R;
import zebrostudio.wallr100.ui.main.MainActivity;
import zebrostudio.wallr100.utils.canaroTextViewUtils.CanaroTextView;

public class GuillotineUtils {

    private static final long RIPPLE_DURATION = 250;

    private MainActivity mActivity;
    private Toolbar mToolbar;
    private View mGuillotineMenu;
    private GuillotineListener mGuillotineListener;
    private boolean mIsGuillotineOpened;
    private GuillotineAnimation mGuillotineAnimationBuilder;
    private View mHamburgerIcon;
    private LinearLayout mExploreLayout;
    private LinearLayout mTopPicksLayout;
    private LinearLayout mCategoriesLayout;
    private LinearLayout mMinimalLayout;
    private LinearLayout mCollectionLayout;
    private LinearLayout mFeedBackLayout;
    private LinearLayout mBuyProLayout;
    private CanaroTextView mExploreGuillotineTitle;
    private CanaroTextView mTopPicksGuillotineTitle;
    private CanaroTextView mCategoriesGuillotineTitle;
    private CanaroTextView mMinimalGuillotineTitle;
    private CanaroTextView mCollectionGuillotineTitle;
    private CanaroTextView mBuyProGuillotineTitle;

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
    }

    private void setToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
    }

    private void inflateAndAddGuillotineMenu(Context context, FrameLayout rootView) {
        mGuillotineMenu = LayoutInflater.from(context).inflate(R.layout.guillotine_layout, null);
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

    public void takeViewObject(GuillotineViewObject guillotineViewObject) {
        mExploreLayout = guillotineViewObject.getmExploreLayout();
        mTopPicksLayout = guillotineViewObject.getmTopPicksLayout();
        mCategoriesLayout = guillotineViewObject.getmCategoriesLayout();
        mMinimalLayout = guillotineViewObject.getmMinimalLayout();
        mCategoriesLayout = guillotineViewObject.getmCategoriesLayout();
        mCollectionLayout = guillotineViewObject.getmCollectionLayout();
        mFeedBackLayout = guillotineViewObject.getmFeedBackLayout();
        mBuyProLayout = guillotineViewObject.getmBuyProLayout();
        mExploreGuillotineTitle = guillotineViewObject.getmExploreTitleView();
        mTopPicksGuillotineTitle = guillotineViewObject.getmTopPicksTitleView();
        mCategoriesGuillotineTitle = guillotineViewObject.getmCategoriesTitleView();
        mMinimalGuillotineTitle = guillotineViewObject.getmMinimalTitleView();
        mCollectionGuillotineTitle = guillotineViewObject.getmCollectionTitleView();
        mBuyProLayout = guillotineViewObject.getmBuyProLayout();

        setMenuItemsClickListener();
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

    public void highLightExploreGuillotineMenuItem(){
        mExploreGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
        mTopPicksGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCategoriesGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mMinimalGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCollectionGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
    }

    public void highLightTopPicksGuillotineMenuItem(){
        mExploreGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mTopPicksGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
        mCategoriesGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mMinimalGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCollectionGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
    }

    public void highLightCategoriesGuillotineMenuItem(){
        mExploreGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mTopPicksGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCategoriesGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
        mMinimalGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCollectionGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
    }

    public void highLightMinimalGuillotineMenuItem(){
        mExploreGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mTopPicksGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCategoriesGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mMinimalGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
        mCollectionGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
    }

    public void highLightCollectionsGuillotineMenuItem(){
        mExploreGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mTopPicksGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCategoriesGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mMinimalGuillotineTitle.setTextColor(Color.parseColor("#ffffff"));
        mCollectionGuillotineTitle.setTextColor(Color.parseColor("#e51c23"));
    }

    public boolean getGuillotineState() {
        return mIsGuillotineOpened;
    }

    public void closeguillotineMenu() {
        mGuillotineAnimationBuilder.close();
    }

}
