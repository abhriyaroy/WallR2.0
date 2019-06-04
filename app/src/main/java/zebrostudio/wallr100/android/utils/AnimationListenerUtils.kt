package zebrostudio.wallr100.android.utils

import androidx.annotation.AnimRes
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils

private class AnimationListener(
  private val onAnimationRepeat: () -> Unit,
  private val onAnimationStart: () -> Unit,
  private val onAnimationEnd: () -> Unit
) : Animation.AnimationListener {
  override fun onAnimationRepeat(animation: Animation) = onAnimationRepeat()
  override fun onAnimationStart(animation: Animation) = onAnimationStart()
  override fun onAnimationEnd(animation: Animation) = onAnimationEnd()
}

fun View.showAnimation(
  @AnimRes animResId: Int,
  fillAfter: Boolean = true,
  onAnimationRepeat: () -> Unit = {},
  onAnimationStart: () -> Unit = {},
  onAnimationEnd: () -> Unit = {}
) = with(AnimationUtils.loadAnimation(context, animResId)) {
  setAnimationListener(AnimationListener(onAnimationRepeat, onAnimationStart, onAnimationEnd))
  this.fillAfter = fillAfter
  startAnimation(this)
}