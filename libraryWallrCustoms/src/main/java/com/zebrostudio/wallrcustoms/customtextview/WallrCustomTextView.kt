package com.zebrostudio.wallrcustoms.customtextview

import android.content.Context
import android.graphics.Color
import androidx.appcompat.widget.AppCompatTextView
import android.util.AttributeSet

class WallrCustomTextView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 1
) : AppCompatTextView(context, attrs, defStyleAttr) {

  init {
    this.setTextColor(Color.WHITE)
    this.typeface = FontCache.getRobotoRegularTypeface(context)
  }
}
