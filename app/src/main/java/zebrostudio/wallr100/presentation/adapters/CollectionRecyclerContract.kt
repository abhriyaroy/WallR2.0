package zebrostudio.wallr100.presentation.adapters

interface CollectionRecyclerContract {

  interface CollectioneRecyclerItemViewHolder {
    fun setImage(imagePath: String)
    fun showSelectedIndicator()
    fun hideSelectedIndicator()
    fun attachClickListener()
    fun attachLongClickListener()
  }

  interface CollectionRecyclerPresenter {
    fun getItemCount(list: List<String>): Int
    fun onBindRepositoryRowViewAtPosition(
      dragSelectItemViewHolder: CollectioneRecyclerItemViewHolder,
      imagePathList: List<String>,
      selectedItemsMap: HashMap<Int, String>,
      position: Int
    )
  }

}