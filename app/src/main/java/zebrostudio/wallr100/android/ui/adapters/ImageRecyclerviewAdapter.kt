package zebrostudio.wallr100.android.ui.adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.image_recyclerview_item.view.imageView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.setPlaceholderColor
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract

class ImageRecyclerviewAdapter(private val presenter: ImageRecyclerItemContract.ImageRecyclerviewPresenter) :
    RecyclerView.Adapter<RecyclerviewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerviewHolder {
    return RecyclerviewHolder(
        parent.inflate(LayoutInflater.from(parent.context), R.layout.image_recyclerview_item),
        parent.context)
  }

  override fun getItemCount(): Int {
    return presenter.getItemCount()
  }

  override fun onBindViewHolder(holder: RecyclerviewHolder, position: Int) {
    presenter.onBindRepositoryRowViewAtPosition(position, holder)
  }

}

class RecyclerviewHolder(
  itemView: View,
  private val context: Context
) : RecyclerView.ViewHolder(itemView),
    ImageRecyclerItemContract.ImageRecyclerItemView {

  private val imageHeight = 180
  private val imageWidth = 250

  override fun setImageviewBackground(colorHexCode: String) {
    itemView.imageView.setPlaceholderColor(colorHexCode)
  }

  override fun setImage(link: String) {
    val options = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .override(imageHeight, imageWidth)
        .centerCrop()

    Glide.with(context)
        .load(link)
        .transition(withCrossFade())
        .apply(options)
        .into(itemView.imageView)
  }

}