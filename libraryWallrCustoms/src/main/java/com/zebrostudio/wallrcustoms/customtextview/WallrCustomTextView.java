package com.zebrostudio.wallrcustoms.customtextview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class WallrCustomTextView extends AppCompatTextView {

  public WallrCustomTextView(Context context) {
    this(context, null);
  }

  public WallrCustomTextView(Context context, AttributeSet attrs){
    this(context, attrs,1);
  }

  public WallrCustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    this.setTextColor(Color.WHITE);
    this.setTypeface(FontCache.getRobotoRegularTypeface(context));
  }
}