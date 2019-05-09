package zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper

import android.graphics.Canvas
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_IDLE
import android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_SWIPE
import android.support.v7.widget.helper.ItemTouchHelper.Callback
import android.support.v7.widget.helper.ItemTouchHelper.DOWN
import android.support.v7.widget.helper.ItemTouchHelper.END
import android.support.v7.widget.helper.ItemTouchHelper.LEFT
import android.support.v7.widget.helper.ItemTouchHelper.RIGHT
import android.support.v7.widget.helper.ItemTouchHelper.START
import android.support.v7.widget.helper.ItemTouchHelper.UP

private const val ALPHA_FULL = 1.0f

class CollectionRecyclerTouchHelperCallback(
  private val adapter: ItemTouchHelperAdapter
) : Callback() {

  override fun isLongPressDragEnabled(): Boolean {
    return true
  }

  override fun isItemViewSwipeEnabled(): Boolean {
    return true
  }

  override fun getMovementFlags(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder
  ): Int {
    return if (recyclerView.layoutManager is GridLayoutManager) {
      val swipeFlags = 0
      val dragFlags =
          UP or DOWN or LEFT or RIGHT
      makeMovementFlags(dragFlags, swipeFlags)
    } else {
      val dragFlags = UP or DOWN
      val swipeFlags = START or END
      return makeMovementFlags(dragFlags, swipeFlags)
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
    if (actionState == ACTION_STATE_SWIPE) {
      val alpha = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.width.toFloat()
      viewHolder.itemView.alpha = alpha
      viewHolder.itemView.translationX = dX
    } else {
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
  }

}