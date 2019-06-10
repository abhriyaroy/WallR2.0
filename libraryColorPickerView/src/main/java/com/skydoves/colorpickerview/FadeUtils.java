package com.skydoves.colorpickerview;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

@SuppressWarnings("WeakerAccess")
public class FadeUtils {
  public static void fadeIn(View view) {
    Animation fadeIn = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_in);
    fadeIn.setFillAfter(true);
    view.startAnimation(fadeIn);
  }

  public static void fadeOut(View view) {
    Animation fadeOut = AnimationUtils.loadAnimation(view.getContext(), R.anim.fade_out);
    fadeOut.setFillAfter(true);
    view.startAnimation(fadeOut);
  }
}
