package zebrostudio.wallr100.android.ui.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.dragselectrecyclerview.DragSelectReceiver
import kotlinx.android.synthetic.main.item_recyclerview_minimal_fragment.view.colorThumbnail
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewItem
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewPresenter

class MinimalImageAdapter(private val minimalRecyclerViewPresenter: MinimalRecyclerViewPresenter) :
    RecyclerView.Adapter<MinimalViewHolder>(), DragSelectReceiver {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MinimalViewHolder {
    return MinimalViewHolder(parent.inflate(LayoutInflater.from(parent.context),
        R.layout.item_recyclerview_minimal_fragment), minimalRecyclerViewPresenter)
  }

  override fun getItemCount(): Int {
    return minimalRecyclerViewPresenter.getItemCount()
  }

  override fun onBindViewHolder(holder: MinimalViewHolder, position: Int) {
    minimalRecyclerViewPresenter.onBindRepositoryRowViewAtPosition(holder, position)
  }

  override fun setSelected(index: Int, selected: Boolean) {

  }

  override fun isSelected(index: Int): Boolean {
    return true
  }

  override fun isIndexSelectable(index: Int): Boolean {
    return true
  }

}

class MinimalViewHolder(
  itemView: View,
  private val minimalRecyclerViewPresenter: MinimalRecyclerViewPresenter
) : RecyclerView.ViewHolder(itemView), MinimalRecyclerViewItem {
  override fun setImageViewColor(colorHexCode: String) {
    itemView.colorThumbnail.setBackgroundColor(Color.parseColor(colorHexCode))
  }

  override fun attachClickListeners() {
    itemView.setOnClickListener {
      minimalRecyclerViewPresenter.handleClick(adapterPosition, this)
    }

    itemView.setOnLongClickListener {
      minimalRecyclerViewPresenter.handleImageLongClick(adapterPosition, this)
      true
    }
  }

}