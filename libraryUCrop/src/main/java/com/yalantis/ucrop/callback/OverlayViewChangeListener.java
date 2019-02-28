package com.yalantis.ucrop.callback;

import android.graphics.RectF;

public interface OverlayViewChangeListener {

  void onCropRectUpdated(RectF cropRect);
}