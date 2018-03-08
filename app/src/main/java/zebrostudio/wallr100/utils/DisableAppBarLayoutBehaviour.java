package zebrostudio.wallr100.utils;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

public class DisableAppBarLayoutBehaviour extends AppBarLayout.Behavior {
    private boolean mEnabled = true;

    public DisableAppBarLayoutBehaviour() {
        super();
    }

    public DisableAppBarLayoutBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return mEnabled && super.onStartNestedScroll(parent, child, directTargetChild, target, nestedScrollAxes);
    }

    public boolean isEnabled() {
        return mEnabled;
    }
}
