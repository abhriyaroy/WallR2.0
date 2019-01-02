package zebrostudio.wallr100.android.ui.detail

import android.os.Bundle
import dagger.android.AndroidInjection
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseActivity
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter.Companion.imageDetails
import zebrostudio.wallr100.presentation.detail.DetailContract.DetailPresenter
import zebrostudio.wallr100.presentation.detail.DetailContract.DetailView
import zebrostudio.wallr100.presentation.search.model.SearchPicturesPresenterEntity
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity

class DetailActivity : BaseActivity(), DetailView {
  private lateinit var presenter: DetailPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    super.onCreate(savedInstanceState)
    presenter.attachView(this)
    setContentView(R.layout.activity_detail)

  }

  override fun getWallpaperImageDetails(): ImagePresenterEntity {
    return intent.getParcelableExtra(imageDetails)
  }

  override fun getSearchImageDetails(): SearchPicturesPresenterEntity {
    return intent.getParcelableExtra(imageDetails)
  }

}
