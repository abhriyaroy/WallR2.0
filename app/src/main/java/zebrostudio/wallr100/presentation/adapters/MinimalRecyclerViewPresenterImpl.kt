package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.android.ui.adapters.MinimalViewHolder
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalPresenter

const val INITIAL_SIZE = 0
const val INITIAL_OFFSET = 1
const val MINIMUM_COLOR_LIST_SIZE = 20

class MinimalRecyclerViewPresenterImpl : MinimalRecyclerViewPresenter {

  private var minimalPresenter: MinimalPresenter? = null
  private var colorList = mutableListOf<String>()
  private var selectedHashMap = HashMap<Int, Boolean>()

  override fun attachMinimalPresenter(presenter: MinimalPresenter) {
    minimalPresenter = presenter
  }

  override fun detachMinimalPresenter() {
    minimalPresenter = null
  }

  override fun setList(colorList: List<String>) {
    this.colorList = colorList.toMutableList()
  }

  override fun getList(): MutableList<String> {
    return colorList
  }

  override fun appendColor(color: String) {
    colorList.add(color)
  }

  override fun getItemCount(): Int {
    return colorList.size
  }

  override fun onBindRepositoryRowViewAtPosition(holder: MinimalViewHolder, position: Int) {
    if (position == INITIAL_SIZE) {
      holder.showAddImageLayout()
      holder.hideSelectedIndicator()
    } else {
      holder.hideAddImageLayout()
      holder.setImageViewColor(colorList[position - INITIAL_OFFSET])
      holder.attachLongClickListener()
      if (selectedHashMap.size != 0) {
        if (selectedHashMap.containsKey(position - INITIAL_OFFSET)) {
          holder.showSelectedIndicator()
        } else {
          holder.hideSelectedIndicator()
        }
      }
    }
    holder.attachClickListener()
  }

  override fun handleClick(
    position: Int,
    itemView: MinimalRecyclerItemContract.MinimalRecyclerViewItem
  ) {
    if (selectedHashMap.size == 0) {
      // open detail screen
    } else {
      toggleSelected(position)
    }
  }

  override fun handleImageLongClick(
    position: Int,
    itemView: MinimalRecyclerItemContract.MinimalRecyclerViewItem
  ) {
    toggleSelected(position)
    minimalPresenter?.handleItemLongClick(position)

  }

  override fun isItemSelectable(index: Int): Boolean {
    return index != INITIAL_SIZE
  }

  override fun isItemSelected(index: Int): Boolean {
    return selectedHashMap.containsKey(index - INITIAL_OFFSET)
  }

  override fun setItemSelected(index: Int, selected: Boolean) {
    if (selected) {
      selectedHashMap[index - INITIAL_OFFSET] = true
    } else {
      selectedHashMap.remove(index - INITIAL_OFFSET)
    }
    minimalPresenter?.updateSelectionChange(index, selectedHashMap.size)
  }

  override fun clearSelectedItems() {
    selectedHashMap.clear()
  }

  override fun isDeletionPossible(): Int {
    return if (colorList.size - selectedHashMap.size >= MINIMUM_COLOR_LIST_SIZE) {
      INITIAL_SIZE
    } else {
      MINIMUM_COLOR_LIST_SIZE - (colorList.size - selectedHashMap.size)
    }
  }

  override fun getSelectedMap(): HashMap<Int, Boolean> {
    return selectedHashMap
  }

  private fun toggleSelected(index: Int) {
    (index - INITIAL_OFFSET).let {
      if (!selectedHashMap.containsKey(it)) {
        selectedHashMap.put(it, true)
      } else {
        selectedHashMap.remove(it)
      }
    }
    minimalPresenter?.updateSelectionChange(index, selectedHashMap.size)
  }

}