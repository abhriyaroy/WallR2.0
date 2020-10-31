package com.afollestad.dragselectrecyclerview

import androidx.annotation.CheckResult

interface DragSelectReceiver {

  @CheckResult fun getItemCount(): Int

  fun setSelected(
    index: Int,
    selected: Boolean
  )

  @CheckResult fun isSelected(index: Int): Boolean

  @CheckResult fun isIndexSelectable(index: Int): Boolean
}
