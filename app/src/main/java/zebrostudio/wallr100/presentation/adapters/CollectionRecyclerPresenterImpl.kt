package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionsRecyclerItemViewHolder
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity

class CollectionRecyclerPresenterImpl : CollectionRecyclerPresenter {

  override fun getItemCount(list: List<CollectionsPresenterEntity>): Int {
    return list.size
  }

  override fun onBindRepositoryRowViewAtPosition(
    collectionsRecyclerItemViewHolder: CollectionsRecyclerItemViewHolder,
    imagePathList: List<CollectionsPresenterEntity>,
    selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>,
    position: Int
  ) {
    collectionsRecyclerItemViewHolder.setTag(imagePathList[position].path)
    collectionsRecyclerItemViewHolder.setImage(imagePathList[position].path)
    collectionsRecyclerItemViewHolder.attachClickListener()
    collectionsRecyclerItemViewHolder.attachLongClickToDragListener()
    if (selectedItemsMap.containsKey(position)) {
      collectionsRecyclerItemViewHolder.showSelectedIndicator()
    } else {
      collectionsRecyclerItemViewHolder.hideSelectedIndicator()
    }
  }

}