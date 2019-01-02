package zebrostudio.wallr100.android.ui.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.image_recyclerview_item.view.imageView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter.Companion.imageDetails
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter.Companion.imageType
import zebrostudio.wallr100.android.ui.detail.DetailActivity
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.*
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

class ImageAdapter(private val presenter: ImageRecyclerItemContract.ImageRecyclerViewPresenter) :
    RecyclerView.Adapter<ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(parent.inflate(LayoutInflater.from(parent.context),
        R.layout.image_recyclerview_item), parent.context, presenter)
  }

  override fun getItemCount(): Int {
    return presenter.getItemCount()
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    presenter.onBindRepositoryRowViewAtPosition(position, holder)
  }

  companion object {
    var imageDetails = "ImageDetails"
    var imageType = "ImageType"
  }

}

class ViewHolder(
  itemView: View,
  private val context: Context,
  private val presenter: ImageRecyclerItemContract.ImageRecyclerViewPresenter
) : RecyclerView.ViewHolder(itemView),
    ImageRecyclerItemContract.ImageRecyclerItemView {

  override fun setImageViewBackgroundAndClickListener(colorHexCode: String) {
    itemView.imageView.setBackgroundColor(Color.parseColor(colorHexCode))
    itemView.setOnClickListener { presenter.notifyImageClicked(adapterPosition, this) }
  }

  override fun setSearchImage(link: String) {
    val options = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .override(context.integerRes(R.integer.recycler_view_adapter_search_image_width),
            context.integerRes(R.integer.recycler_view_adapter_search_image_height))
        .centerCrop()
    loadAndShowImage(link, options)
  }

  override fun setWallpaperImage(link: String) {
    val options = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .override(context.integerRes(R.integer.recycler_view_adapter_wallpaper_image_width),
            context.integerRes(R.integer.recycler_view_adapter_wallpaper_image_height))
        .centerCrop()
    loadAndShowImage(link, options)
  }

  override fun showSearchImageDetails(searchImage: SearchPicturesPresenterEntity) {
    val intent = Intent(context, DetailActivity::class.java)
    intent.putExtra(imageDetails, searchImage)
    intent.putExtra(imageType, SEARCH)
    context.startActivity(intent)
  }

  override fun showWallpaperImageDetails(wallpaperImage: ImagePresenterEntity) {
    val intent = Intent(context, DetailActivity::class.java)
    intent.putExtra(imageDetails, wallpaperImage)
    intent.putExtra(imageType, WALLPAPERS)
    context.startActivity(intent)
  }

  private fun loadAndShowImage(link: String, options: RequestOptions) {
    Glide.with(context)
        .load(link)
        .transition(withCrossFade())
        .apply(options)
        .into(itemView.imageView)
  }

}