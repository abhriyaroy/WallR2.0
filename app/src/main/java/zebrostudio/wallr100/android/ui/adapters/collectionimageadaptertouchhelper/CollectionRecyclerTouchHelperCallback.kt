package zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper.*

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
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder
  ): Int {
    return if (recyclerView.layoutManager is GridLayoutManager) {
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

}
