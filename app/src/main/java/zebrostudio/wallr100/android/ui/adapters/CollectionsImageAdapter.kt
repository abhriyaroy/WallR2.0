package zebrostudio.wallr100.android.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_recyclerview_collections.view.imageView
import kotlinx.android.synthetic.main.item_recyclerview_minimal_fragment.view.selectedIndicatorIcon
import kotlinx.android.synthetic.main.item_recyclerview_minimal_fragment.view.selectedOverlay
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper.ItemTouchHelperAdapter
import zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper.OnStartDragListener
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionsRecyclerItemViewHolder
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity

interface CollectionsImageAdapterCallbacks {
  fun handleItemMoved(fromPosition: Int, toPosition: Int)
  fun handleClick(index: Int)
}

class CollectionsImageAdapter(
  private val collectionsImageAdapterCallback: CollectionsImageAdapterCallbacks,
  private val startDragListener: OnStartDragListener,
  private val presenter: CollectionRecyclerPresenter
) : androidx.recyclerview.widget.RecyclerView.Adapter<CollectionsImageViewHolder>(), ItemTouchHelperAdapter {

  private var imagePathList = mutableListOf<CollectionsPresenterEntity>()
  private var selectedHashMap = HashMap<Int, CollectionsPresenterEntity>()

  override fun onCreateViewHolder(
    viewGroupParent: ViewGroup,
    viewType: Int
  ): CollectionsImageViewHolder {
    return CollectionsImageViewHolder(
        viewGroupParent.inflate(LayoutInflater.from(viewGroupParent.context),
            R.layout.item_recyclerview_collections), viewGroupParent.context,
        collectionsImageAdapterCallback, startDragListener)
  }

  override fun getItemCount(): Int {
    return presenter.getItemCount(imagePathList)
  }

  override fun onBindViewHolder(viewHolder: CollectionsImageViewHolder, position: Int) {
    presenter.onBindRepositoryRowViewAtPosition(viewHolder, imagePathList, selectedHashMap,
        position)
  }

  override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
    collectionsImageAdapterCallback.handleItemMoved(fromPosition, toPosition)
    return true
  }

  fun getImagePathList() = imagePathList

  fun setImagesList(list: List<CollectionsPresenterEntity>) {
    imagePathList = list.toMutableList()
  }

  fun getSelectedItemsMap() = selectedHashMap

  fun clearSelectedItemsMap() {
    selectedHashMap.clear()
  }

  fun clearImagesList() {
    imagePathList.clear()
  }
}

class CollectionsImageViewHolder(
  itemView: View,
  private val context: Context,
  private val callback: CollectionsImageAdapterCallbacks,
  private val startDragListener: OnStartDragListener
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
    CollectionsRecyclerItemViewHolder {

  override fun setImage(imagePath: String) {
    Glide.with(context)
        .load(imagePath)
        .into(itemView.imageView)
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

  override fun attachLongClickToDragListener() {
    itemView.setOnLongClickListener {
      startDragListener.onStartDrag(this)
      true
    }
  }
}