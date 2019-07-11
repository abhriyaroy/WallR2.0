package zebrostudio.wallr100.android.ui.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Color.*
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.dragselectrecyclerview.DragSelectReceiver
import kotlinx.android.synthetic.main.item_recyclerview_minimal_fragment.view.*
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerContract.DragSelectItemPresenter
import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerContract.DragSelectItemViewHolder

interface DragSelectImageAdapterCallbacks {
  fun setItemSelected(index: Int, selected: Boolean)
  fun isItemSelected(index: Int): Boolean
  fun isItemSelectable(index: Int): Boolean
  fun handleClick(index: Int)
  fun handleLongClick(index: Int): Boolean
}

class DragSelectImageAdapter(
  private val callback: DragSelectImageAdapterCallbacks,
  private val presenter: DragSelectItemPresenter
) : RecyclerView.Adapter<MinimalViewHolder>(), DragSelectReceiver {

  private var colorList = mutableListOf<String>()
  private var selectedHashMap = HashMap<Int, String>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MinimalViewHolder {
    return MinimalViewHolder(parent.inflate(LayoutInflater.from(parent.context),
      R.layout.item_recyclerview_minimal_fragment), parent.context, callback)
  }

  override fun getItemCount(): Int {
    return presenter.getItemCount(colorList)
  }

  override fun onBindViewHolder(holder: MinimalViewHolder, position: Int) {
    presenter.onBindRepositoryRowViewAtPosition(holder.getDragSelectItemViewHolder(), colorList,
      selectedHashMap, position)
  }

  override fun setSelected(index: Int, selected: Boolean) {
    callback.setItemSelected(index, selected)
  }

  override fun isSelected(index: Int): Boolean {
    return callback.isItemSelected(index)
  }

  override fun isIndexSelectable(index: Int): Boolean {
    return callback.isItemSelectable(index)
  }

  fun getColorList() = colorList

  fun setColorList(list: List<String>) {
    colorList = list.toMutableList()
  }

  fun addColorToList(hexValue: String) {
    colorList.add(hexValue)
  }

  fun getSelectedItemsMap() = selectedHashMap

  fun addToSelectedItemsMap(itemPosition: Int, hexValue: String) {
    selectedHashMap[itemPosition] = hexValue
  }

  fun removeItemFromSelectedItemsMap(itemPosition: Int) {
    selectedHashMap.remove(itemPosition)
  }

  fun clearSelectedItemsMap() = selectedHashMap.clear()

}

class MinimalViewHolder(
  itemView: View,
  private val context: Context,
  private val callback: DragSelectImageAdapterCallbacks
) : ViewHolder(itemView), DragSelectItemViewHolder {

  override fun showAddImageLayout() {
    itemView.colorThumbnail.setBackgroundColor(context.colorRes(R.color.black))
    itemView.addColorIcon.visible()
  }

  override fun hideAddImageLayout() {
    itemView.addColorIcon.gone()
  }

  override fun setImageViewColor(colorHexCode: String) {
    itemView.colorThumbnail.setBackgroundColor(parseColor(colorHexCode))
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
      callback.handleClick(adapterPosition)
    }
  }

  override fun attachLongClickListener() {
    itemView.setOnLongClickListener {
      callback.handleLongClick(adapterPosition)
    }
  }

  fun getDragSelectItemViewHolder(): DragSelectItemViewHolder {
    return this
  }

}