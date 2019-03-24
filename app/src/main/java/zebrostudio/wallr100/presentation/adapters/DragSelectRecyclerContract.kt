package zebrostudio.wallr100.presentation.adapters

interface DragSelectRecyclerContract {

  interface DragSelectItemViewHolder {
    fun showAddImageLayout()
    fun hideAddImageLayout()
    fun setImageViewColor(colorHexCode: String)
    fun showSelectedIndicator()
    fun hideSelectedIndicator()
    fun attachClickListener()
    fun attachLongClickListener()
  }

  interface DragSelectItemPresenter {
    fun getItemCount(list: List<String>): Int
    fun onBindRepositoryRowViewAtPosition(
      dragSelectItemViewHolder: DragSelectItemViewHolder,
      colorList: List<String>,
      selectedItemsMap: HashMap<Int, Boolean>,
      position: Int
    )
  }

}