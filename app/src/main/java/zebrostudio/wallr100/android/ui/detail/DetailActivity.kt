package zebrostudio.wallr100.android.ui.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_detail.authorImage
import kotlinx.android.synthetic.main.activity_detail.authorName
import kotlinx.android.synthetic.main.activity_detail.imageView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter.Companion.imageDetails
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter.Companion.imageType
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType
import zebrostudio.wallr100.presentation.detail.DetailContract.DetailPresenter
import zebrostudio.wallr100.presentation.detail.DetailContract.DetailView
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import javax.inject.Inject

class DetailActivity : BaseActivity(), DetailView {
  @Inject lateinit var presenter: DetailPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    presenter.attachView(this)
    setContentView(R.layout.activity_detail)
    presenter.setImageType(
        intent.getSerializableExtra(imageType) as ImageListType)

  }

  override fun getWallpaperImageDetails(): ImagePresenterEntity {
    return intent.getParcelableExtra(imageDetails)
  }

  override fun getSearchImageDetails(): SearchPicturesPresenterEntity {
    return intent.getParcelableExtra(imageDetails)
  }

  override fun setAuthorDetails(name: String, profileImageLink: String) {
    System.out.println(name)
    System.out.println(profileImageLink)
    authorName.text = name
    val options = RequestOptions()
        .placeholder(R.drawable.ic_user_white)
        .dontAnimate()
    Glide.with(this)
        .load(profileImageLink)
        .apply(options)
        .into(authorImage)
  }

  override fun showImage(lowQualityLink: String, highQualityLink: String) {
    val placeHolderOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
    val mainImageOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
    Glide.with(this)
        .load(highQualityLink)
        .thumbnail(Glide.with(this)
            .load(lowQualityLink)
            .apply(placeHolderOptions))
        .apply(mainImageOptions)
        .into(imageView)
  }

}
