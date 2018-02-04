package zebrostudio.wallr100.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import javax.inject.Inject;

import zebrostudio.wallr100.R;
import zebrostudio.wallr100.di.ActivityContext;
import zebrostudio.wallr100.di.ApplicationContext;
import zebrostudio.wallr100.ui.main.MainActivity;

public class GuillotineUtils {

    private static final long RIPPLE_DURATION = 250;
    private Activity mActivity;
    private Toolbar mToolbar;
    private View mGuillotineMenu;
    private GuillotineListener mGuillotineListener;
    private boolean mIsGuillotineOpened;
    private GuillotineAnimation mGuillotineAnimationBuilder;
    private View mHamburgerIcon;
    LinearLayout mExploreBackLayout;
    LinearLayout mTopPicksLayout;
    LinearLayout mCategoriesLayout;
    LinearLayout mMinimalLayout;
    LinearLayout mCollectionLayout;
    LinearLayout mFeedBackLayout;
    LinearLayout mBuyProLayout;

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

        this.mExploreBackLayout = guillotineViewObject.getmExploreLayout();
        this.mTopPicksLayout = guillotineViewObject.getmTopPicksLayout();
        this.mCategoriesLayout = guillotineViewObject.getmCategoriesLayout();
        this.mMinimalLayout = guillotineViewObject.getmMinimalLayout();
        this.mCategoriesLayout = guillotineViewObject.getmCategoriesLayout();
        this.mCollectionLayout = guillotineViewObject.getmCollectionLayout();
        this.mFeedBackLayout = guillotineViewObject.getmFeedBackLayout();
        this.mBuyProLayout = guillotineViewObject.getmBuyProLayout();

        setMenuItemsClickListener();

    }

    private void setMenuItemsClickListener() {
        mExploreBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mActivity.getBaseContext(),"hellooo",Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean getGuillotineState() {
        return mIsGuillotineOpened;
    }

}
