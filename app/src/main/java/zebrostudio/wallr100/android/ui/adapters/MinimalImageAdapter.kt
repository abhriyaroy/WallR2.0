package zebrostudio.wallr100.android.ui.adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.dragselectrecyclerview.DragSelectReceiver
import kotlinx.android.synthetic.main.item_recyclerview_minimal_fragment.view.addColorIcon
import kotlinx.android.synthetic.main.item_recyclerview_minimal_fragment.view.colorThumbnail
import kotlinx.android.synthetic.main.item_recyclerview_minimal_fragment.view.selectedIndicatorIcon
import kotlinx.android.synthetic.main.item_recyclerview_minimal_fragment.view.selectedOverlay
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewItem
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewPresenter

class MinimalImageAdapter(private val minimalRecyclerViewPresenter: MinimalRecyclerViewPresenter) :
    RecyclerView.Adapter<MinimalViewHolder>(), DragSelectReceiver {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MinimalViewHolder {
    return MinimalViewHolder(parent.inflate(LayoutInflater.from(parent.context),
        R.layout.item_recyclerview_minimal_fragment), parent.context, minimalRecyclerViewPresenter)
  }

  override fun getItemCount(): Int {
    return minimalRecyclerViewPresenter.getItemCount()
  }

  override fun onBindViewHolder(holder: MinimalViewHolder, position: Int) {
    minimalRecyclerViewPresenter.onBindRepositoryRowViewAtPosition(holder, position)
  }

  override fun setSelected(index: Int, selected: Boolean) {
    minimalRecyclerViewPresenter.setItemSelected(index, selected)
  }

  override fun isSelected(index: Int): Boolean {
    return minimalRecyclerViewPresenter.isItemSelected(index)
  }

  override fun isIndexSelectable(index: Int): Boolean {
    return minimalRecyclerViewPresenter.isItemSelectable(index)
  }

}

class MinimalViewHolder(
  itemView: View,
  private val context: Context,
  private val minimalRecyclerViewPresenter: MinimalRecyclerViewPresenter
) : RecyclerView.ViewHolder(itemView), MinimalRecyclerViewItem {

  override fun showAddImageLayout() {
    itemView.colorThumbnail.setBackgroundColor(context.colorRes(R.color.black))
    itemView.addColorIcon.visible()
  }

  override fun hideAddImageLayout() {
    itemView.addColorIcon.gone()
  }

  override fun setImageViewColor(colorHexCode: String) {
    itemView.colorThumbnail.setBackgroundColor(Color.parseColor(colorHexCode))
  }

  override fun showSelectedIndicator() {
    System.out.println("show selector indicator")
    itemView.selectedOverlay.visible()
    itemView.selectedIndicatorIcon.visible()
  }

  override fun hideSelectedIndicator() {
    itemView.selectedOverlay.gone()
    itemView.selectedIndicatorIcon.gone()
  }

  override fun attachClickListener() {
    System.out.println("attach single click listener $adapterPosition")
    itemView.setOnClickListener {
      System.out.println("single click listener")
      minimalRecyclerViewPresenter.handleClick(adapterPosition, this)
    }
  }

  override fun attachLongClickListener() {
    System.out.println("attach long click listener $adapterPosition")
    itemView.setOnLongClickListener {
      minimalRecyclerViewPresenter.handleImageLongClick(adapterPosition, this)
      true
    }
  }

}