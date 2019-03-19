package zebrostudio.wallr100.android.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.LinearLayout
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

fun withDelayOnMain(delay: Long, block: () -> Unit) {
  Handler(Looper.getMainLooper()).postDelayed(Runnable(block), delay)
}