package com.zebrostudio.wallrcustoms.customflatbutton

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import com.zebrostudio.wallrcustoms.R

class FlatButton : Button, View.OnTouchListener {
  //Custom values
  private var isShadowEnabled = true
  private var buttonColor: Int = 0
  private var shadowColor: Int = 0
  private var shadowHeight: Int = 0
  private var cornerRadius: Int = 0

  //Native values
  private var paddingLeftValue: Int = 0
  private var paddingRightValue: Int = 0
  private var paddingTopValue: Int = 0
  private var paddingBottomValue: Int = 0

  //Background drawable
  private var pressedDrawable: Drawable? = null
  private var unpressedDrawable: Drawable? = null

  private var isShadowColorDefined = false

  constructor(context: Context) : super(context) {
    init()
    this.setOnTouchListener(this)
  }

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    init()
    parseAttrs(context, attrs)
    this.setOnTouchListener(this)
  }

  constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs,
      defStyle) {
    init()
    parseAttrs(context, attrs)
    this.setOnTouchListener(this)
  }

  override fun onFinishInflate() {
    super.onFinishInflate()
    //Update background color
    refresh()
  }

  override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
    when (motionEvent.action) {
      MotionEvent.ACTION_DOWN -> {
        updateBackground(pressedDrawable)
        this.setPadding(paddingLeftValue, paddingTopValue + shadowHeight, paddingRightValue, paddingBottomValue)
      }
      MotionEvent.ACTION_MOVE -> {
        val r = Rect()
        view.getLocalVisibleRect(r)
        if (!r.contains(motionEvent.x.toInt(),
                motionEvent.y.toInt() + 3 * shadowHeight) && !r.contains(
                motionEvent.x.toInt(), motionEvent.y.toInt() - 3 * shadowHeight)) {
          updateBackground(unpressedDrawable)
          this.setPadding(paddingLeftValue, paddingTopValue + shadowHeight, paddingRightValue,
              paddingBottomValue + shadowHeight)
        }
      }
      MotionEvent.ACTION_OUTSIDE, MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
        updateBackground(unpressedDrawable)
        this.setPadding(paddingLeftValue, paddingTopValue + shadowHeight, paddingRightValue,
            paddingBottomValue + shadowHeight)
      }
    }
    return false
  }

  private fun init() {
    //Init default values
    isShadowEnabled = true
    val resources = resources ?: return
    buttonColor = resources.getColor(R.color.fbutton_default_color)
    shadowColor = resources.getColor(R.color.fbutton_default_shadow_color)
    shadowHeight = resources.getDimensionPixelSize(R.dimen.fbutton_default_shadow_height)
    cornerRadius = resources.getDimensionPixelSize(R.dimen.fbutton_default_conner_radius)
  }

  @SuppressLint("ResourceAsColor", "ResourceType", "CustomViewStyleable")
  private fun parseAttrs(context: Context, attrs: AttributeSet) {
    //Load from custom attributes
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FButton) ?: return
    for (i in 0 until typedArray.indexCount) {
      val attr = typedArray.getIndex(i)
      if (attr == R.styleable.FButton_shadowEnabled) {
        isShadowEnabled = typedArray.getBoolean(attr, true) //Default is true
      } else if (attr == R.styleable.FButton_buttonColor) {
        buttonColor = typedArray.getColor(attr, R.color.fbutton_default_color)
      } else if (attr == R.styleable.FButton_shadowColor) {
        shadowColor = typedArray.getColor(attr, R.color.fbutton_default_shadow_color)
        isShadowColorDefined = true
      } else if (attr == R.styleable.FButton_shadowHeight) {
        shadowHeight =
            typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_shadow_height)
      } else if (attr == R.styleable.FButton_cornerRadius) {
        cornerRadius =
            typedArray.getDimensionPixelSize(attr, R.dimen.fbutton_default_conner_radius)
      }
    }
    typedArray.recycle()

    //Get paddingLeft, paddingRight
    val attrsArray = intArrayOf(android.R.attr.paddingLeft, // 0
        android.R.attr.paddingRight)// 1
    val ta = context.obtainStyledAttributes(attrs, attrsArray) ?: return
    paddingLeftValue = ta.getDimensionPixelSize(0, 0)
    paddingRightValue = ta.getDimensionPixelSize(1, 0)
    ta.recycle()

    //Get paddingTop, paddingBottom
    val attrsArray2 = intArrayOf(android.R.attr.paddingTop, // 0
        android.R.attr.paddingBottom)// 1
    val ta1 = context.obtainStyledAttributes(attrs, attrsArray2) ?: return
    paddingTopValue = ta1.getDimensionPixelSize(0, 0)
    paddingBottomValue = ta1.getDimensionPixelSize(1, 0)
    ta1.recycle()
  }

  private fun refresh() {
    val alpha = Color.alpha(buttonColor)
    val hsv = FloatArray(3)
    Color.colorToHSV(buttonColor, hsv)
    hsv[2] *= 0.8f // value component
    //if shadow color was not defined, generate shadow color = 80% brightness
    if (!isShadowColorDefined) {
      shadowColor = Color.HSVToColor(alpha, hsv)
    }
    //Create pressed background and unpressed background drawables

    if (this.isEnabled) {
      if (isShadowEnabled) {
        pressedDrawable = createDrawable(cornerRadius, Color.TRANSPARENT, buttonColor)
        unpressedDrawable = createDrawable(cornerRadius, buttonColor, shadowColor)
      } else {
        shadowHeight = 0
        pressedDrawable = createDrawable(cornerRadius, shadowColor, Color.TRANSPARENT)
        unpressedDrawable = createDrawable(cornerRadius, buttonColor, Color.TRANSPARENT)
      }
    } else {
      Color.colorToHSV(buttonColor, hsv)
      hsv[1] *= 0.25f // saturation component
      shadowColor = Color.HSVToColor(alpha, hsv)
      val disabledColor = shadowColor
      // Disabled button does not have shadow
      pressedDrawable = createDrawable(cornerRadius, disabledColor, Color.TRANSPARENT)
      unpressedDrawable = createDrawable(cornerRadius, disabledColor, Color.TRANSPARENT)
    }
    updateBackground(unpressedDrawable)
    //Set padding
    this.setPadding(paddingLeftValue, paddingTopValue + shadowHeight, paddingRightValue,
        paddingBottomValue + shadowHeight)
  }

  private fun updateBackground(background: Drawable?) {
    if (background == null) return
    //Set button background
    if (Build.VERSION.SDK_INT >= 18) {
      this.background = background
    } else {
      this.setBackgroundDrawable(background)
    }
  }

  private fun createDrawable(radius: Int, topColor: Int, bottomColor: Int): LayerDrawable {

    val outerRadius =
        floatArrayOf(radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat(),
            radius.toFloat(), radius.toFloat(), radius.toFloat(), radius.toFloat())

    //Top
    val topRoundRect = RoundRectShape(outerRadius, null, null)
    val topShapeDrawable = ShapeDrawable(topRoundRect)
    with(topShapeDrawable) { paint.color = topColor }
    //Bottom
    val roundRectShape = RoundRectShape(outerRadius, null, null)
    val bottomShapeDrawable = ShapeDrawable(roundRectShape)
    with(bottomShapeDrawable) { paint.color = bottomColor }
    //Create array
    val drawArray = arrayOf<Drawable>(bottomShapeDrawable, topShapeDrawable)
    val layerDrawable = LayerDrawable(drawArray)

    //Set shadow height
    if (isShadowEnabled && topColor != Color.TRANSPARENT) {
      //unpressed drawable
      layerDrawable.setLayerInset(0, 0, 0, 0, 0)  /*index, left, top, right, bottom*/
    } else {
      //pressed drawable
      layerDrawable.setLayerInset(0, 0, shadowHeight, 0, 0)  /*index, left, top, right, bottom*/
    }
    layerDrawable.setLayerInset(1, 0, 0, 0, shadowHeight)  /*index, left, top, right, bottom*/

    return layerDrawable
  }

  //Setter
  fun setShadowEnabled(isShadowEnabled: Boolean) {
    this.isShadowEnabled = isShadowEnabled
    setShadowHeight(0)
    refresh()
  }

  fun setButtonColor(buttonColor: Int) {
    this.buttonColor = buttonColor
    refresh()
  }

  fun setShadowColor(shadowColor: Int) {
    this.shadowColor = shadowColor
    isShadowColorDefined = true
    refresh()
  }

  fun setShadowHeight(shadowHeight: Int) {
    this.shadowHeight = shadowHeight
    refresh()
  }

  fun setCornerRadius(cornerRadius: Int) {
    this.cornerRadius = cornerRadius
    refresh()
  }

  fun setFButtonPadding(left: Int, top: Int, right: Int, bottom: Int) {
    paddingLeftValue = left
    paddingRightValue = right
    paddingTopValue = top
    paddingBottomValue = bottom
    refresh()
  }

  override fun setEnabled(enabled: Boolean) {
    super.setEnabled(enabled)
    refresh()
  }

  //Getter
  fun isShadowEnabled(): Boolean {
    return isShadowEnabled
  }

  fun getButtonColor(): Int {
    return buttonColor
  }

  override fun getShadowColor(): Int {
    return shadowColor
  }

  fun getShadowHeight(): Int {
    return shadowHeight
  }

  fun getCornerRadius(): Int {
    return cornerRadius
  }

}