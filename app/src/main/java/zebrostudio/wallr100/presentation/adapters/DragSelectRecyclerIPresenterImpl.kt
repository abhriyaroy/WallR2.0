package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerContract.DragSelectItemPresenter
import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerContract.DragSelectItemViewHolder
import zebrostudio.wallr100.presentation.minimal.INITIAL_OFFSET
import zebrostudio.wallr100.presentation.minimal.INITIAL_SIZE

class DragSelectRecyclerIPresenterImpl : DragSelectItemPresenter {

  override fun getItemCount(list: List<String>): Int {
    return list.size + INITIAL_OFFSET
  }

  override fun onBindRepositoryRowViewAtPosition(
    dragSelectItemViewHolder: DragSelectItemViewHolder,
    colorList: List<String>,
    selectedItemsMap: HashMap<Int, String>,
    position: Int
  ) {
    if (position == INITIAL_SIZE) {
      dragSelectItemViewHolder.showAddImageLayout()
      dragSelectItemViewHolder.hideSelectedIndicator()
    } else {
      dragSelectItemViewHolder.hideAddImageLayout()
      dragSelectItemViewHolder.setImageViewColor(colorList[position - INITIAL_OFFSET])
      dragSelectItemViewHolder.attachLongClickListener()
      dragSelectItemViewHolder.hideSelectedIndicator()
      if (selectedItemsMap.size != INITIAL_SIZE) {
        if (selectedItemsMap.containsKey(position - INITIAL_OFFSET)) {
          dragSelectItemViewHolder.showSelectedIndicator()
        }
      }
    }
    dragSelectItemViewHolder.attachClickListener()
  }

}