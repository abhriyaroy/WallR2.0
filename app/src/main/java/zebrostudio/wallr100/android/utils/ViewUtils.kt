package zebrostudio.wallr100.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.support.annotation.LayoutRes
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.zebrostudio.wallrcustoms.customtextview.WallrCustomTextView
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
  toast: Toast,
  window: Window,
  offsetX: Int = context.getDimensionInPixelSize(R.dimen.toolbar_menu_toast_x_axis_offset),
  offsetY: Int = context.getDimensionInPixelSize(R.dimen.toolbar_menu_toast_y_axis_offset)
) {
  Rect().let { rect ->
    window.decorView.getWindowVisibleDisplayFrame(rect)
    val viewLocation = IntArray(2)
    getLocationInWindow(viewLocation)
    val viewTop = viewLocation[1] - rect.top
    val metrics = DisplayMetrics()
    window.windowManager.defaultDisplay.getMetrics(metrics)
    val widthMeasureSpec =
        View.MeasureSpec.makeMeasureSpec(metrics.widthPixels, View.MeasureSpec.UNSPECIFIED)
    val heightMeasureSpec =
        View.MeasureSpec.makeMeasureSpec(metrics.heightPixels, View.MeasureSpec.UNSPECIFIED)
    toast.view.measure(widthMeasureSpec, heightMeasureSpec)
    val toastWidth = toast.view.measuredWidth
    val toastX = rect.right - toastWidth - offsetX
    val toastY = viewTop + height + offsetY
    toast.setGravity(Gravity.START or Gravity.TOP, toastX, toastY)
  }
}

fun ViewGroup.inflate(
  inflater: LayoutInflater, @LayoutRes layoutRes: Int,
  root: ViewGroup = this,
  attachToRoot: Boolean = false
) = inflater.inflate(layoutRes, root, attachToRoot)!!

@SuppressLint("ResourceType")
fun LinearLayout.setMenuItemColorRed(context: Context) {
  this.findViewById<WallrCustomTextView>(R.id.textviewGuillotineMenuItem)
      .setTextColor(Color.parseColor(context.getString(R.color.accent)))
}

@SuppressLint("ResourceType")
fun LinearLayout.setMenuItemColorWhite(context: Context) {
  this.findViewById<WallrCustomTextView>(R.id.textviewGuillotineMenuItem)
      .setTextColor(Color.parseColor(context.getString(R.color.white)))
}

fun RelativeLayout.disable(context: Context) {
  findViewById<ImageView>(R.id.operationImageView).apply {
    alpha = 0.3f
  }
  findViewById<TextView>(R.id.operationTextView).apply {
    setTextColor(context.colorRes(R.color.dove_gray))
  }
}

fun RelativeLayout.enable(context: Context) {
  findViewById<ImageView>(R.id.operationImageView).apply {
    alpha = 1.0f
  }
  findViewById<TextView>(R.id.operationTextView).apply {
    setTextColor(context.colorRes(R.color.white))
  }
}

fun withDelayOnMain(delay: Long, block: () -> Unit) {
  Handler(Looper.getMainLooper()).postDelayed(Runnable(block), delay)
}