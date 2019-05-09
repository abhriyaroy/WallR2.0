package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionsRecyclerItemViewHolder
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity

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
    dragSelectItemViewHolder.attachLongClickToDragListener()
    if (selectedItemsMap.containsKey(position)) {
      dragSelectItemViewHolder.showSelectedIndicator()
    } else {
      dragSelectItemViewHolder.hideSelectedIndicator()
    }
  }

}