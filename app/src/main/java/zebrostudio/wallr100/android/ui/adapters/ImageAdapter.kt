package zebrostudio.wallr100.android.ui.adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import kotlinx.android.synthetic.main.item_recyclerview_image.view.*
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.ImageLoader
import zebrostudio.wallr100.android.ui.detail.images.DetailActivity
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

class ImageAdapter(private val presenter: ImageRecyclerItemContract.ImageRecyclerViewPresenter,
  private val imageLoader: ImageLoader) :
    RecyclerView.Adapter<ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(parent.inflate(LayoutInflater.from(parent.context),
      R.layout.item_recyclerview_image), parent.context, presenter, imageLoader)
  }

  override fun getItemCount(): Int {
    return presenter.getItemCount()
  }

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int
  ) {
    presenter.onBindRepositoryRowViewAtPosition(position, holder)
  }

}

class ViewHolder(
  itemView: View,
  private val context: Context,
  private val presenter: ImageRecyclerItemContract.ImageRecyclerViewPresenter,
  private val imageLoader: ImageLoader
) : RecyclerView.ViewHolder(itemView),
    ImageRecyclerItemContract.ImageRecyclerItemView {

  override fun configureImageView(backgroundColorHex: String) {
    itemView.imageView.setBackgroundColor(Color.parseColor(backgroundColorHex))
    itemView.setOnClickListener { presenter.handleImageClicked(adapterPosition, this) }
  }

  override fun setSearchImage(link: String) {
    loadAndShowImage(link)
  }

  override fun setWallpaperImage(link: String) {
    loadAndShowImage(link)
  }

  override fun showSearchImageDetails(searchImage: SearchPicturesPresenterEntity) {
    context.startActivity(DetailActivity.getCallingIntent(context, searchImage))
  }

  override fun showWallpaperImageDetails(wallpaperImage: ImagePresenterEntity) {
    context.startActivity(DetailActivity.getCallingIntent(context, wallpaperImage))
  }

  private fun loadAndShowImage(link: String) {
    itemView.imageView.tag = link
    imageLoader.loadWithFixedSize(context,
      link,
      itemView.imageView,
      DiskCacheStrategy.ALL,
      withCrossFade(),
      context.integerRes(R.integer.recycler_view_adapter_wallpaper_image_width),
      context.integerRes(R.integer.recycler_view_adapter_wallpaper_image_height))
  }

}