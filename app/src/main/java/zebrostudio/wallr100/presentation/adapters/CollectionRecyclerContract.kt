package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity

interface CollectionRecyclerContract {

  interface CollectionsRecyclerItemViewHolder {
    fun setImage(imagePath: String)
    fun showSelectedIndicator()
    fun hideSelectedIndicator()
    fun attachClickListener()
  }

  interface CollectionRecyclerPresenter {
    fun getItemCount(list: List<CollectionsPresenterEntity>): Int
    fun onBindRepositoryRowViewAtPosition(
      dragSelectItemViewHolder: CollectionsRecyclerItemViewHolder,
      imagePathList: List<CollectionsPresenterEntity>,
      selectedItemsMap: HashMap<Int, CollectionsPresenterEntity>,
      position: Int
    )
  }

}