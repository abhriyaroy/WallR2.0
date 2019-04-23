package com.skydoves.colorpickerview;

import android.content.Context;

/** SizeUtils a util class for resizing scales. */
@SuppressWarnings("WeakerAccess")
class SizeUtils {
  /** changes dp size to px size. */
  public static int dp2Px(Context context, int dp) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dp * scale + 0.5f);
  }
}
