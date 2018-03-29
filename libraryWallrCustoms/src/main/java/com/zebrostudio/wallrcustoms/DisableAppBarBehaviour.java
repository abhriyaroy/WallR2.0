package com.zebrostudio.wallrcustoms;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;

public class DisableAppBarBehaviour extends AppBarLayout.Behavior {
  private boolean enabled = true;

  public DisableAppBarBehaviour() {
    super();
  }

  public DisableAppBarBehaviour(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean onStartNestedScroll(CoordinatorLayout parent, AppBarLayout child,
      View directTargetChild, View target, int nestedScrollAxes) {
    return enabled && super.onStartNestedScroll(parent, child, directTargetChild, target,
        nestedScrollAxes);
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isEnabled() {
    return enabled;
  }
}