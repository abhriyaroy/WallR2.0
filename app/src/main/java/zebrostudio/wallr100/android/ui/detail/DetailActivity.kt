package zebrostudio.wallr100.android.ui.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.zagum.expandicon.ExpandIconView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import dagger.android.AndroidInjection
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_detail.addToCollectionImageLayout
import kotlinx.android.synthetic.main.activity_detail.authorImage
import kotlinx.android.synthetic.main.activity_detail.authorName
import kotlinx.android.synthetic.main.activity_detail.crystallizeImageLayout
import kotlinx.android.synthetic.main.activity_detail.downloadImageLayout
import kotlinx.android.synthetic.main.activity_detail.editAndSetImageLayout
import kotlinx.android.synthetic.main.activity_detail.expandIconView
import kotlinx.android.synthetic.main.activity_detail.imageView
import kotlinx.android.synthetic.main.activity_detail.setWallpaperImageLayout
import kotlinx.android.synthetic.main.activity_detail.shareImageLayout
import kotlinx.android.synthetic.main.activity_detail.slidingPanel
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter.Companion.imageDetails
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter.Companion.imageType
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
    setUpExpandPanel()
    attachClickListeners()
  }

  override fun getWallpaperImageDetails(): ImagePresenterEntity {
    return intent.getParcelableExtra(imageDetails)
  }

  override fun getSearchImageDetails(): SearchPicturesPresenterEntity {
    return intent.getParcelableExtra(imageDetails)
  }

  override fun setAuthorDetails(name: String, profileImageLink: String) {
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
        .listener(object : RequestListener<Drawable> {
          override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
          ): Boolean {
            presenter.notifyHighQualityImageLoadFailed()
            return false
          }

          override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
          ): Boolean {
            return false
          }

        })
        .into(imageView)
  }


  override fun getStoragePermission() {

  }

  override fun showImageLoadError() {
    Toasty.error(this, getString(R.string.detail_activity_unable_to_load_hd_image_error))
  }

  private fun setUpExpandPanel() {
    expandIconView.setState(ExpandIconView.LESS, false)
    slidingPanel.setParallaxOffset(40)
    slidingPanel.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
      override fun onPanelSlide(panel: View, slideOffset: Float) {
        // Do nothing
      }

      override fun onPanelStateChanged(
        panel: View,
        previousState: SlidingUpPanelLayout.PanelState,
        newState: SlidingUpPanelLayout.PanelState
      ) {
        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
          expandIconView.setState(ExpandIconView.MORE, true)
        } else if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
          expandIconView.setState(ExpandIconView.LESS, true)
        }
      }
    })
  }

  private fun attachClickListeners() {
    setWallpaperImageLayout.setOnClickListener { presenter.notifyQuickSetClick() }
    downloadImageLayout.setOnClickListener { presenter.notifyDownloadClick() }
    crystallizeImageLayout.setOnClickListener { presenter.notifyCrystallizeClick() }
    editAndSetImageLayout.setOnClickListener { presenter.notifyEditSetClick() }
    addToCollectionImageLayout.setOnClickListener { presenter.notifyAddToCollectionClick() }
    shareImageLayout.setOnClickListener { presenter.notifyShareClick() }
  }

}
