package zebrostudio.wallr100.android.utils

import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.*
import android.view.View.*
import android.view.View.MeasureSpec.UNSPECIFIED
import android.view.View.MeasureSpec.makeMeasureSpec
import android.widget.Toast
import androidx.annotation.LayoutRes
import zebrostudio.wallr100.R

fun View.setOnDebouncedClickListener(onClick: (v: View) -> Unit) {
  setOnClickListener(object : DebouncedOnClickListener() {
    override fun onDebouncedClick(v: View) = onClick(v)
  })
}

fun View.gone() {
  this.visibility = GONE
}

fun View.invisible() {
  this.visibility = INVISIBLE
}

fun View.visible() {
  this.visibility = VISIBLE
}

fun View.menuTitleToast(
  context: Context,
  message: String,
  window: Window,
  duration: Int = Toast.LENGTH_SHORT,
  offsetX: Int = context.getDimensionInPixelSize(R.dimen.toolbar_menu_toast_x_axis_offset),
  offsetY: Int = context.getDimensionInPixelSize(R.dimen.toolbar_menu_toast_y_axis_offset)
) {
  Toast.makeText(context, message, duration).let { toast ->
    Rect().let { rect ->
      window.decorView.getWindowVisibleDisplayFrame(rect)
      val viewLocation = IntArray(2)
      getLocationInWindow(viewLocation)
      val viewTop = viewLocation[1] - rect.top
      val metrics = DisplayMetrics()
      window.windowManager.defaultDisplay.getMetrics(metrics)
      val widthMeasureSpec =
          makeMeasureSpec(metrics.widthPixels, UNSPECIFIED)
      val heightMeasureSpec =
          makeMeasureSpec(metrics.heightPixels, UNSPECIFIED)
      toast.view?.measure(widthMeasureSpec, heightMeasureSpec)
      val toastWidth = toast.view?.measuredWidth ?: DEFAULT_TOAST_WIDTH
      val toastX = rect.right - toastWidth - offsetX
      val toastY = viewTop + height + offsetY
      toast.setGravity(Gravity.START or Gravity.TOP, toastX, toastY)
    }
    toast.show()
  }
}

fun ViewGroup.inflate(
        inflater: LayoutInflater, @LayoutRes layoutRes: Int,
        root: ViewGroup = this,
        attachToRoot: Boolean = false
) = inflater.inflate(layoutRes, root, attachToRoot)!!

fun withDelayOnMain(delay: Long, block: () -> Unit) {
  Handler(Looper.getMainLooper()).postDelayed(Runnable(block), delay)
}

const val DEFAULT_TOAST_WIDTH = 100
