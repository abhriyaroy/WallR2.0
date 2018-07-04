package zebrostudio.wallr100.utils

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun View.setOnDebouncedClickListener(onClick: (v: View) -> Unit) {
  setOnClickListener(object : DebouncedOnClickListener() {
    override fun onDebouncedClick(v: View) = onClick(v)
  })
}

fun ViewGroup.inflate(
  inflater: LayoutInflater, @LayoutRes layoutRes: Int,
  root: ViewGroup = this,
  attachToRoot: Boolean = false
) = inflater.inflate(layoutRes, root, attachToRoot)!!