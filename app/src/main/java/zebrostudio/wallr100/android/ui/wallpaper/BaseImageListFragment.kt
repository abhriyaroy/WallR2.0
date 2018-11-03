package zebrostudio.wallr100.android.ui.wallpaper

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import dagger.android.support.AndroidSupportInjection
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import kotlinx.android.synthetic.main.fragment_wallpaper_list.recyclerView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.adapters.ImageAdapter
import zebrostudio.wallr100.android.utils.GridItemDecorator
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerItemContract
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.*
import zebrostudio.wallr100.presentation.wallpaper.BaseImageListView
import zebrostudio.wallr100.presentation.wallpaper.model.ImagePresenterEntity
import java.util.concurrent.TimeUnit.*
import javax.inject.Inject

abstract class BaseImageListFragment : Fragment(), BaseImageListView {

  @Inject
  internal lateinit var imageRecyclerViewPresenter: ImageRecyclerItemContract.ImageRecyclerViewPresenter

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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecyclerView()
  }

  override fun showLoader() {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun hideLoader() {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun showGenericErrorMessageView() {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun hideGenericErrorMessageView() {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun showImageList(list: List<ImagePresenterEntity>) {
    System.out.println("displayimagelist" + "size" + list.size)
    imageRecyclerViewPresenter.setWallpaperImageList(list)
    recyclerviewAdapter?.notifyDataSetChanged()
    recyclerView.visible()
  }

  override fun hideRefreshing() {
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
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

}
