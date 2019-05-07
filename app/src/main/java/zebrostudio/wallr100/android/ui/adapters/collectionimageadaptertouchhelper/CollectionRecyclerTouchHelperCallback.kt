package zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper

import android.graphics.Canvas
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.Callback

private const val ALPHA_FULL = 1.0f

class CollectionRecyclerTouchHelperCallback(private val adapter: ItemTouchHelperAdapter) : Callback() {

  override fun isLongPressDragEnabled(): Boolean {
    return true
  }

  override fun isItemViewSwipeEnabled(): Boolean {
    return false
  }

  override fun getMovementFlags(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder
  ): Int {
    val swipeFlags = 0
    return if (recyclerView.layoutManager is GridLayoutManager) {
      val dragFlags =
          ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
      makeMovementFlags(dragFlags, swipeFlags)
    } else {
      val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
      makeMovementFlags(dragFlags, swipeFlags)
    }
  }

  override fun onMove(
    recyclerView: RecyclerView,
    source: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder
  ): Boolean {
    if (source.itemViewType != target.itemViewType) {
      return false
    }
    adapter.onItemMove(source.adapterPosition, target.adapterPosition)
    return true
  }

  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, i: Int) {
    // Do Nothing
  }

  override fun onChildDraw(
    c: Canvas,
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    dX: Float,
    dY: Float,
    actionState: Int,
    isCurrentlyActive: Boolean
  ) {
    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
      val alpha = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.width.toFloat()
      viewHolder.itemView.alpha = alpha
      viewHolder.itemView.translationX = dX
    } else {
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
  }

  override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
    if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
      if (viewHolder is ItemTouchHelperViewHolder) {
        val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
        itemViewHolder.onItemSelected()
      }
    }
    super.onSelectedChanged(viewHolder, actionState)
  }

  override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
    super.clearView(recyclerView, viewHolder)
    viewHolder.itemView.alpha = ALPHA_FULL
    if (viewHolder is ItemTouchHelperViewHolder) {
      val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
      itemViewHolder.onItemClear()
    }
  }
}