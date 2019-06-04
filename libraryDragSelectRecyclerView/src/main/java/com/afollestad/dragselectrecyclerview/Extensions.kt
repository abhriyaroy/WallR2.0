package com.afollestad.dragselectrecyclerview

import android.content.Context
import android.view.MotionEvent
import androidx.annotation.DimenRes
import androidx.annotation.Px
import androidx.recyclerview.widget.RecyclerView

@Px internal fun Context.dimen(@DimenRes res: Int): Int {
  return resources.getDimensionPixelSize(res)
}

internal typealias ListAdapter<T> = androidx.recyclerview.widget.RecyclerView.Adapter<T>

internal fun ListAdapter<*>.isEmpty(): Boolean {
  return itemCount == 0
}

internal fun androidx.recyclerview.widget.RecyclerView.getItemPosition(e: MotionEvent): Int {
  val v =
      findChildViewUnder(e.x, e.y) ?: return androidx.recyclerview.widget.RecyclerView.NO_POSITION
  return getChildAdapterPosition(v)
}
