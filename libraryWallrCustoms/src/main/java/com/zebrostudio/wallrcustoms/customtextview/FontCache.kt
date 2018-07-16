package com.zebrostudio.wallrcustoms.customtextview

import android.content.Context
import android.graphics.Typeface

internal object FontCache {

  fun getRobotoRegularTypeface(context: Context): Typeface {
    return Typeface.createFromAsset(context.assets, "fonts/Roboto-Regular.ttf")
  }

}