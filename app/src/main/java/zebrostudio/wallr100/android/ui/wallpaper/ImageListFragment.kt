package zebrostudio.wallr100.android.ui.wallpaper

import androidx.lifecycle.Lifecycle
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import dagger.android.support.AndroidSupportInjection
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.fragment_image_list.view.*
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.ImageLoader
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter
import zebrostudio.wallr100.android.utils.*
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract.ImageRecyclerViewPresenter
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.WALLPAPERS
import zebrostudio.wallr100.presentation.wallpaper.ImageListContract.ImageListPresenter
import zebrostudio.wallr100.presentation.wallpaper.ImageListContract.ImageListView
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class ImageListFragment : Fragment(), ImageListView {

  @Inject
  internal lateinit var imageRecyclerViewPresenter: ImageRecyclerViewPresenter
  @Inject
  internal lateinit var presenter: ImageListPresenter
  @Inject
  internal lateinit var imageLoader: ImageLoader

  private var recyclerviewAdapter: ImageAdapter? = null
  private var spinkitView: SpinKitView? = null
  private var recyclerView: RecyclerView? = null
  private var errorInfoRelativeLayout: LinearLayout? = null
  private var swipeRefreshLayout: WaveSwipeRefreshLayout? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AndroidSupportInjection.inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return container?.inflate(inflater, R.layout.fragment_image_list)
  }

  override fun onViewCreated(
    view: View,
    savedInstanceState: Bundle?
  ) {
    super.onViewCreated(view, savedInstanceState)
    initViews(view)
    configureSwipeRefreshLayout()
    presenter.attachView(this)
    val parentFragment = this.parentFragment as WallpaperFragment
    presenter.setImageListType(
      parentFragment.fragmentTag,
      FragmentPagerItem.getPosition(arguments)
    )
    presenter.fetchImages(false)
  }

  override fun onDestroy() {
    super.onDestroy()
    presenter.detachView()
  }

  override fun internetAvailability() = activity?.checkDataConnection()!!

  override fun showLoader() {
    spinkitView?.visible()
  }

  override fun hideLoader() {
    spinkitView?.gone()
  }

  override fun showNoInternetMessageView() {
    errorInfoRelativeLayout?.visible()
  }

  override fun showImageList(list: List<ImagePresenterEntity>) {
    recyclerView?.visible()
    imageRecyclerViewPresenter.setWallpaperImageList(list)
    recyclerviewAdapter?.notifyDataSetChanged()
  }

  override fun hideRefreshing() {
    swipeRefreshLayout?.isRefreshing = false
  }

  override fun hideAllLoadersAndMessageViews() {
    hideLoader()
    errorInfoRelativeLayout?.gone()
    recyclerView?.gone()
  }

  override fun getScope(): ScopeProvider {
    return AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
  }

  private fun initViews(view: View) {
    spinkitView = view.spinkitView
    errorInfoRelativeLayout = view.errorInfoRelativeLayout
    recyclerView = view.recyclerView
    swipeRefreshLayout = view.swipeRefreshLayout
    initRecyclerView()
  }

  private fun initRecyclerView() {
    val layoutManager =
        GridLayoutManager(context, integerRes(R.integer.recycler_view_span_count))
    recyclerView?.layoutManager = layoutManager
    recyclerviewAdapter = ImageAdapter(imageRecyclerViewPresenter, imageLoader)
    val scaleInAdapter = ScaleInAnimationAdapter(recyclerviewAdapter)
    scaleInAdapter.setDuration(MILLISECONDS.toMillis(500).toInt())
    recyclerView?.addItemDecoration(
      RecyclerViewItemDecorator(
        integerRes(R.integer.recycler_view_grid_spacing_px),
        integerRes(R.integer.recycler_view_grid_size)
      )
    )
    recyclerView?.adapter = scaleInAdapter
    imageRecyclerViewPresenter.setListType(WALLPAPERS)
  }

  private fun configureSwipeRefreshLayout() {
    swipeRefreshLayout?.setColorSchemeColors(Color.WHITE, Color.WHITE)
    swipeRefreshLayout?.setWaveRGBColor(
      integerRes(R.integer.swipe_refresh_rgb_wave),
      integerRes(R.integer.swipe_refresh_rgb_wave),
      integerRes(R.integer.swipe_refresh_rgb_wave)
    )
    if (swipeRefreshLayout?.isRefreshing == false) {
      swipeRefreshLayout?.setOnRefreshListener {
        presenter.fetchImages(true)
      }
    }
  }

}
