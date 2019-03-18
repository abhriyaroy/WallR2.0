package zebrostudio.wallr100.presentation.adapters

import zebrostudio.wallr100.android.ui.adapters.MinimalViewHolder
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalPresenter

interface MinimalRecyclerItemContract {

  interface MinimalRecyclerViewItem {
    fun showAddImageLayout()
    fun hideAddImageLayout()
    fun setImageViewColor(colorHexCode: String)
    fun showSelectedIndicator()
    fun hideSelectedIndicator()
    fun attachClickListener()
    fun attachLongClickListener()
  }

  interface MinimalRecyclerViewPresenter {
    fun attachMinimalPresenter(presenter: MinimalPresenter)
    fun detachMinimalPresenter()
    fun appendList(colorList: List<String>)
    fun appendColor(color: String)
    fun getItemCount(): Int
    fun onBindRepositoryRowViewAtPosition(holder: MinimalViewHolder, position: Int)
    fun handleClick(position: Int, itemView: MinimalRecyclerViewItem)
    fun handleImageLongClick(position: Int, itemView: MinimalRecyclerViewItem)
  }

}