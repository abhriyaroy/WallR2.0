package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.android.ui.adapters.MinimalViewHolder

interface MinimalRecyclerItemContract {

  interface MinimalRecyclerViewItem {
    fun setImageViewColor(colorHexCode: String)
    fun attachClickListeners()
  }

  interface MinimalRecyclerViewPresenter {
    fun appendList(colorList: List<String>)
    fun appendColor(color: String)
    fun getItemCount(): Int
    fun onBindRepositoryRowViewAtPosition(holder: MinimalViewHolder, position: Int)
    fun handleClick(position: Int, itemView: MinimalRecyclerViewItem)
    fun handleImageLongClick(position: Int, itemView: MinimalRecyclerViewItem)
  }

}