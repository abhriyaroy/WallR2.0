package zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper.Callback
import androidx.recyclerview.widget.ItemTouchHelper.DOWN
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.UP

class CollectionRecyclerTouchHelperCallback(
  private val adapter: ItemTouchHelperAdapter
) : Callback() {

  override fun isLongPressDragEnabled(): Boolean {
    return true
  }

  override fun isItemViewSwipeEnabled(): Boolean {
    return false
  }

  override fun getMovementFlags(
    recyclerView: androidx.recyclerview.widget.RecyclerView,
    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder
  ): Int {
    return if (recyclerView.layoutManager is androidx.recyclerview.widget.GridLayoutManager) {
      val swipeFlags = 0
      val dragFlags = UP or DOWN or LEFT or RIGHT
      makeMovementFlags(dragFlags, swipeFlags)
    } else {
      val dragFlags = UP or DOWN
      val swipeFlags = START or END
      return makeMovementFlags(dragFlags, swipeFlags)
    }
  }

  override fun onMove(
    recyclerView: androidx.recyclerview.widget.RecyclerView,
    source: androidx.recyclerview.widget.RecyclerView.ViewHolder,
    target: androidx.recyclerview.widget.RecyclerView.ViewHolder
  ): Boolean {
    if (source.itemViewType != target.itemViewType) {
      return false
    }
    adapter.onItemMove(source.adapterPosition, target.adapterPosition)
    return true
  }

  override fun onSwiped(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder, i: Int) {
    // Do Nothing
  }

}