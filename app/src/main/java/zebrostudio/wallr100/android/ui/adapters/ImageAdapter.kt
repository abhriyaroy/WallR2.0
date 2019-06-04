package zebrostudio.wallr100.android.ui.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_recyclerview_image.view.imageView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.detail.images.DetailActivity
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

class ImageAdapter(private val presenter: ImageRecyclerItemContract.ImageRecyclerViewPresenter) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(parent.inflate(LayoutInflater.from(parent.context),
        R.layout.item_recyclerview_image), parent.context, presenter)
  }

  override fun getItemCount(): Int {
    return presenter.getItemCount()
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    presenter.onBindRepositoryRowViewAtPosition(position, holder)
  }

}

class ViewHolder(
  itemView: View,
  private val context: Context,
  private val presenter: ImageRecyclerItemContract.ImageRecyclerViewPresenter
) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView),
    ImageRecyclerItemContract.ImageRecyclerItemView {

  override fun setImageViewBackgroundAndAttachClickListener(colorHexCode: String) {
    itemView.imageView.setBackgroundColor(Color.parseColor(colorHexCode))
    itemView.setOnClickListener { presenter.handleImageClicked(adapterPosition, this) }
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
    context.startActivity(DetailActivity.getCallingIntent(context, searchImage))
  }

  override fun showWallpaperImageDetails(wallpaperImage: ImagePresenterEntity) {
    context.startActivity(DetailActivity.getCallingIntent(context, wallpaperImage))
  }

  private fun loadAndShowImage(link: String, options: RequestOptions) {
    Glide.with(context)
        .load(link)
        .transition(withCrossFade())
        .apply(options)
        .into(itemView.imageView)
  }

}