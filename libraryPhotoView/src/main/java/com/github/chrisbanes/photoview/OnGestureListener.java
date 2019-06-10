package com.github.chrisbanes.photoview;

interface OnGestureListener {

  void onDrag(float dx, float dy);

  void onFling(float startX, float startY, float velocityX,
      float velocityY);

  void onScale(float scaleFactor, float focusX, float focusY);
}