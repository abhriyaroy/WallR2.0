package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionsRecyclerItemViewHolder
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import zebrostudio.wallr100.presentation.minimal.INITIAL_OFFSET
import zebrostudio.wallr100.presentation.minimal.INITIAL_SIZE

class CollectionRecyclerPresenterImpl : CollectionRecyclerPresenter {

  override fun getItemCount(list: List<CollectionsPresenterEntity>): Int {
    return list.size
  }

  override fun onBindRepositoryRowViewAtPosition(
    dragSelectItemViewHolder: CollectionsRecyclerItemViewHolder,
    imagePathList: List<CollectionsPresenterEntity>,
    selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>,
    position: Int
  ) {
    dragSelectItemViewHolder.setImage(imagePathList[position].path)
    dragSelectItemViewHolder.attachClickListener()
    dragSelectItemViewHolder.attachLongClickListener()
    if (selectedItemsMap.size != INITIAL_SIZE) {
      if (selectedItemsMap.containsKey(position - INITIAL_OFFSET)) {
        dragSelectItemViewHolder.showSelectedIndicator()
      }
    }
  }

}