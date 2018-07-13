package com.zebrostudio.wallrcustoms

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View

class DisableAppBarBehaviour(context: Context, attrs: AttributeSet) : AppBarLayout.Behavior(context,
    attrs) {
  var isEnabled = true

  override fun onStartNestedScroll(
    parent: CoordinatorLayout, child: AppBarLayout,
    directTargetChild: View, target: View, nestedScrollAxes: Int
  ): Boolean {
    return isEnabled && super.onStartNestedScroll(parent, child, directTargetChild, target,
        nestedScrollAxes)
  }
}