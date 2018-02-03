package zebrostudio.wallr100.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.support.v7.widget.Toolbar;

import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import javax.inject.Inject;

import zebrostudio.wallr100.R;
import zebrostudio.wallr100.ui.MainActivity;

public class GuillotineUtils {

    private static final long RIPPLE_DURATION = 250;
    private Activity mActivity;
    private Toolbar mToolbar;
    private View mGuillotineMenu;
    private GuillotineListener mGuillotineListener;
    private boolean mIsGuillotineOpened;
    private GuillotineAnimation mGuillotineAnimationBuilder;
    private View mHamburgerIcon;

    @Inject
    public GuillotineUtils(MainActivity activity) {
        mActivity = activity;
    }

    public void setToolbar(Toolbar toolbar) {
        mToolbar = toolbar;
    }

    public void inflateAndAddGuillotineMenu(Context context, FrameLayout rootView) {
        mGuillotineMenu = LayoutInflater.from(context).inflate(R.layout.guillotine_layout, null);
        mHamburgerIcon = mActivity.findViewById(R.id.content_hamburger);
        rootView.addView(mGuillotineMenu);
    }

    public void setGuillotineListener() {
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

    public boolean getGuillotineState() {
        return mIsGuillotineOpened;
    }

    public void guillotineAnimationBuilder() {
        mGuillotineAnimationBuilder = new GuillotineAnimation.GuillotineBuilder(
                mGuillotineMenu, mGuillotineMenu.findViewById(R.id.guillotine_hamburger), mHamburgerIcon)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(mToolbar)
                .setGuillotineListener(mGuillotineListener)
                .setClosedOnStart(true)
                .build();
    }

}
