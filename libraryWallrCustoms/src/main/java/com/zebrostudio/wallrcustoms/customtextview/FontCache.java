package com.zebrostudio.wallrcustoms.customtextview;

import android.content.Context;
import android.graphics.Typeface;

class FontCache {

  static Typeface getRobotoRegularTypeface(Context context) {
    return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
  }
}
