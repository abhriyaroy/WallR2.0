package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectioneRecyclerItemViewHolder
import zebrostudio.wallr100.presentation.minimal.INITIAL_OFFSET
import zebrostudio.wallr100.presentation.minimal.INITIAL_SIZE

class CollectionRecyclerPresenterImpl : CollectionRecyclerPresenter {

  override fun getItemCount(list: List<String>): Int {
    return list.size
  }

  override fun onBindRepositoryRowViewAtPosition(
    dragSelectItemViewHolder: CollectioneRecyclerItemViewHolder,
    imagePathList: List<String>,
    selectedItemsMap: HashMap<Int, String>,
    position: Int
  ) {
    dragSelectItemViewHolder.setImage(imagePathList[position])
    dragSelectItemViewHolder.attachClickListener()
    dragSelectItemViewHolder.attachLongClickListener()
    if (selectedItemsMap.size != INITIAL_SIZE) {
      if (selectedItemsMap.containsKey(position - INITIAL_OFFSET)) {
        dragSelectItemViewHolder.showSelectedIndicator()
      }
    }
  }

}