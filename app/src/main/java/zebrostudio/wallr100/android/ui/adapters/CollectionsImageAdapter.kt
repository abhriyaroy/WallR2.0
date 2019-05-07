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
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionsRecyclerItemViewHolder

interface CollectionsImageAdapterCallbacks {
  fun setItemSelected(index: Int, selected: Boolean)
  fun isItemSelected(index: Int): Boolean
  fun isItemSelectable(index: Int): Boolean
  fun handleClick(index: Int)
  fun handleLongClick(index: Int): Boolean
}

class CollectionsImageAdapter(
  private val collectionsImageAdapterCallbacks: CollectionsImageAdapterCallbacks,
  private val presenter: CollectionRecyclerPresenter
) : RecyclerView.Adapter<CollectionsImageViewHolder>() {

  private var imagePathList = mutableListOf<String>()
  private var selectedHashMap = HashMap<Int, String>()

  override fun onCreateViewHolder(
    viewGroupParent: ViewGroup,
    viewType: Int
  ): CollectionsImageViewHolder {
    return CollectionsImageViewHolder(
        viewGroupParent.inflate(LayoutInflater.from(viewGroupParent.context),
            R.layout.item_recyclerview_collections), viewGroupParent.context,
        collectionsImageAdapterCallbacks)
  }

  override fun getItemCount(): Int {
    return presenter.getItemCount(imagePathList)
  }

  override fun onBindViewHolder(viewHolder: CollectionsImageViewHolder, position: Int) {
    presenter.onBindRepositoryRowViewAtPosition(viewHolder, imagePathList, selectedHashMap,
        position)
  }

}

class CollectionsImageViewHolder(
  itemView: View,
  private val context: Context,
  private val callback: CollectionsImageAdapterCallbacks
) : RecyclerView.ViewHolder(itemView), CollectionsRecyclerItemViewHolder {

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

  override fun attachLongClickListener() {
    itemView.setOnLongClickListener {
      callback.handleLongClick(adapterPosition)
    }
  }
}