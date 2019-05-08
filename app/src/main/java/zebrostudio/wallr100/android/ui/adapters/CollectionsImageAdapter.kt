package zebrostudio.wallr100.android.ui.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_recyclerview_collections.view.imageView
import kotlinx.android.synthetic.main.item_recyclerview_minimal_fragment.view.selectedIndicatorIcon
import kotlinx.android.synthetic.main.item_recyclerview_minimal_fragment.view.selectedOverlay
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper.ItemTouchHelperAdapter
import zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper.ItemTouchHelperViewHolder
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionsRecyclerItemViewHolder
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import zebrostudio.wallr100.presentation.minimal.INITIAL_OFFSET

interface CollectionsImageAdapterCallbacks {
  fun handleItemMoved(fromPosition: Int, toPosition: Int)
  fun handleClick(index: Int)
}

class CollectionsImageAdapter(
  private val collectionsImageAdapterCallback: CollectionsImageAdapterCallbacks,
  private val presenter: CollectionRecyclerPresenter
) : RecyclerView.Adapter<CollectionsImageViewHolder>(), ItemTouchHelperAdapter {

  private var imagePathList = mutableListOf<CollectionsPresenterEntity>()
  private var selectedHashMap = HashMap<Int, CollectionsPresenterEntity>()

  override fun onCreateViewHolder(
    viewGroupParent: ViewGroup,
    viewType: Int
  ): CollectionsImageViewHolder {
    return CollectionsImageViewHolder(
        viewGroupParent.inflate(LayoutInflater.from(viewGroupParent.context),
            R.layout.item_recyclerview_collections), viewGroupParent.context,
        collectionsImageAdapterCallback)
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

  fun setColorList(list: List<CollectionsPresenterEntity>) {
    imagePathList = list.toMutableList()
    notifyDataSetChanged()
  }

  fun addImageToList(image: CollectionsPresenterEntity) {
    imagePathList.add(image)
    notifyItemInserted(imagePathList.size - INITIAL_OFFSET)
  }

  fun getSelectedItemsMap() = selectedHashMap

  fun addToSelectedItemsMap(itemPosition: Int, entity: CollectionsPresenterEntity) {
    selectedHashMap[itemPosition] = entity
  }

  fun removeItemFromSelectedItemsMap(itemPosition: Int) {
    selectedHashMap.remove(itemPosition)
  }

  fun clearSelectedItemsMap() = selectedHashMap.clear()

}

class CollectionsImageViewHolder(
  itemView: View,
  private val context: Context,
  private val callback: CollectionsImageAdapterCallbacks
) : RecyclerView.ViewHolder(itemView),
    CollectionsRecyclerItemViewHolder,
    ItemTouchHelperViewHolder {

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

  override fun onItemSelected() {
    println("view holder item selected")
  }

  override fun onItemClear() {
    println("On item clear")
  }
}