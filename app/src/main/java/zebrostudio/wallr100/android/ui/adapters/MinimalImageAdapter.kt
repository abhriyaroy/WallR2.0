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
import zebrostudio.wallr100.presentation.minimal.MinimalContract.ItemViewHolder
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalPresenter

class MinimalImageAdapter(private val presenter: MinimalPresenter) :
    RecyclerView.Adapter<MinimalViewHolder>(), DragSelectReceiver {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MinimalViewHolder {
    return MinimalViewHolder(parent.inflate(LayoutInflater.from(parent.context),
        R.layout.item_recyclerview_minimal_fragment), parent.context, presenter)
  }

  override fun getItemCount(): Int {
    return presenter.getItemCount()
  }

  override fun onBindViewHolder(holder: MinimalViewHolder, position: Int) {
    presenter.onBindRepositoryRowViewAtPosition(holder, position)
  }

  override fun setSelected(index: Int, selected: Boolean) {
    presenter.setItemSelected(index, selected)
  }

  override fun isSelected(index: Int): Boolean {
    return presenter.isItemSelected(index)
  }

  override fun isIndexSelectable(index: Int): Boolean {
    return presenter.isItemSelectable(index)
  }

}

class MinimalViewHolder(
  itemView: View,
  private val context: Context,
  private val presenter: MinimalPresenter
) : RecyclerView.ViewHolder(itemView), ItemViewHolder {

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
    itemView.selectedOverlay.visible()
    itemView.selectedIndicatorIcon.visible()
  }

  override fun hideSelectedIndicator() {
    itemView.selectedOverlay.gone()
    itemView.selectedIndicatorIcon.gone()
  }

  override fun attachClickListener() {
    itemView.setOnClickListener {
      presenter.handleClick(adapterPosition, this)
    }
  }

  override fun attachLongClickListener() {
    itemView.setOnLongClickListener {
      presenter.handleImageLongClick(adapterPosition, this)
      true
    }
  }

}