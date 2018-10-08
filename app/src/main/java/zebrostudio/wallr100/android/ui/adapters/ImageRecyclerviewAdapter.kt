package zebrostudio.wallr100.android.ui.adapters

import android.content.Context
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
import zebrostudio.wallr100.android.utils.dimenRes
import zebrostudio.wallr100.android.utils.inflate
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

  override fun setImageviewBackground(colorHexCode: String) {
    itemView.imageView.setBackgroundColor(Color.parseColor(colorHexCode))
  }

  override fun setImage(link: String) {
    val options = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .override(context.dimenRes(R.dimen.recycler_view_adapter_image_height),
            context.dimenRes(R.dimen.recycler_view_adapter_image_height))
        .centerCrop()

    Glide.with(context)
        .load(link)
        .transition(withCrossFade())
        .apply(options)
        .into(itemView.imageView)
  }

}