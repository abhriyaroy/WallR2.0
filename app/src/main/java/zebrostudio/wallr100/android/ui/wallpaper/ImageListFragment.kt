package zebrostudio.wallr100.android.ui.wallpaper

import android.arch.lifecycle.Lifecycle
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import dagger.android.support.AndroidSupportInjection
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.fragment_wallpaper_list.errorInfoRelativeLayout
import kotlinx.android.synthetic.main.fragment_wallpaper_list.recyclerView
import kotlinx.android.synthetic.main.fragment_wallpaper_list.spinkitView
import kotlinx.android.synthetic.main.fragment_wallpaper_list.swipeRefreshLayout
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter
import zebrostudio.wallr100.android.utils.GridItemDecorator
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.*
import zebrostudio.wallr100.presentation.wallpaper.explore.ImageListContract.ImageListPresenter
import zebrostudio.wallr100.presentation.wallpaper.explore.ImageListContract.ImageListView
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import java.util.concurrent.TimeUnit.*
import javax.inject.Inject

class ImageListFragment : Fragment(), ImageListView {

  @Inject
  internal lateinit var imageRecyclerViewPresenter: ImageRecyclerItemContract.ImageRecyclerViewPresenter
  @Inject internal lateinit var presenter: ImageListPresenter

  private var recyclerviewAdapter: ImageAdapter? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidSupportInjection.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return container?.inflate(inflater, R.layout.fragment_wallpaper_list)
  }

  override fun onResume() {
    super.onResume()

  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecyclerView()
    configureSwipeRefreshLayout()
    presenter.attachView(this)
    val parentFragment = this.parentFragment as WallpaperFragment
    presenter.setImageListType(parentFragment.fragmentTag,
        FragmentPagerItem.getPosition(arguments))
    presenter.fetchImages()
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.detachView()
  }

  override fun showLoader() {
    spinkitView.visible()
  }

  override fun hideLoader() {
    spinkitView.gone()
  }

  override fun showNoInternetMessageView() {
    errorInfoRelativeLayout.visible()
  }

  override fun showImageList(list: List<ImagePresenterEntity>) {
    imageRecyclerViewPresenter.setWallpaperImageList(list)
    recyclerviewAdapter?.notifyDataSetChanged()
    recyclerView.visible()
  }

  override fun hideRefreshing() {
    swipeRefreshLayout.isRefreshing = false
  }

  override fun hideAllLoadersAndMessageViews() {
    hideLoader()
    hideRefreshing()
    errorInfoRelativeLayout.gone()
    recyclerView.gone()
  }

  override fun getScope(): ScopeProvider {
    return AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
  }

  private fun initRecyclerView() {
    val layoutManager =
        GridLayoutManager(context, context!!.integerRes(R.integer.recycler_view_span_count))
    recyclerView.layoutManager = layoutManager
    recyclerviewAdapter = ImageAdapter(imageRecyclerViewPresenter)
    val scaleInAdapter = ScaleInAnimationAdapter(recyclerviewAdapter)
    scaleInAdapter.setDuration(MILLISECONDS.toMillis(500).toInt())
    recyclerView.addItemDecoration(
        GridItemDecorator(context!!.integerRes(R.integer.recycler_view_grid_spacing_px),
            context!!.integerRes(R.integer.recycler_view_grid_size)))
    recyclerView.adapter = scaleInAdapter
    imageRecyclerViewPresenter.setListType(WALLPAPERS)
  }

  private fun configureSwipeRefreshLayout() {
    swipeRefreshLayout.setColorSchemeColors(Color.WHITE, Color.WHITE)
    swipeRefreshLayout.setWaveRGBColor(context!!.integerRes(R.integer.swipe_refresh_rgb_wave),
        context!!.integerRes(R.integer.swipe_refresh_rgb_wave),
        context!!.integerRes(R.integer.swipe_refresh_rgb_wave))
    swipeRefreshLayout.setOnRefreshListener {
      presenter.fetchImages()
    }
  }

}
