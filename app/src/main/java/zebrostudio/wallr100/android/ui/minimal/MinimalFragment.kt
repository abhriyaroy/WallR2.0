package zebrostudio.wallr100.android.ui.minimal

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.dragselectrecyclerview.DragSelectTouchListener
import com.afollestad.dragselectrecyclerview.Mode.RANGE
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_minimal.minimalFragmentRecyclerView
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.ui.adapters.MinimalImageAdapter
import zebrostudio.wallr100.android.utils.RecyclerViewItemDecorator
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.presentation.adapters.MinimalRecyclerItemContract.MinimalRecyclerViewPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalPresenter
import zebrostudio.wallr100.presentation.minimal.MinimalContract.MinimalView
import javax.inject.Inject

class MinimalFragment : BaseFragment(), MinimalView {

  @Inject
  internal lateinit var presenter: MinimalPresenter
  @Inject
  internal lateinit var recyclerAdapterPresenter: MinimalRecyclerViewPresenter
  private var minimalImageAdapter: MinimalImageAdapter? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    AndroidSupportInjection.inject(this)
    return container?.inflate(inflater, R.layout.fragment_minimal)
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
    presenter.attachMinimalImageRecyclerViewPresenter(recyclerAdapterPresenter)
    recyclerAdapterPresenter.attachMinimalPresenter(presenter)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    initRecyclerView()
    presenter.handleViewCreated()
  }

  override fun onDestroy() {
    presenter.detachView()
    presenter.detachMinimalImageRecyclerViewPresenter()
    recyclerAdapterPresenter.detachMinimalPresenter()
    super.onDestroy()
  }

  override fun showColors() {
    minimalImageAdapter?.notifyDataSetChanged()
  }

  override fun showUnableToGetColorsErrorMessage() {
    context!!.errorToast(getString(R.string.minimal_fragment_unable_to_get_colors_error_message))
  }

  override fun showGenericErrorMessage() {
    context!!.errorToast(getString(R.string.generic_error_message))
  }

  private fun initRecyclerView() {
    GridLayoutManager(context,
        context!!.integerRes(R.integer.minimal_image_recycler_view_span_count)).let {
      minimalFragmentRecyclerView.addItemDecoration(
          RecyclerViewItemDecorator(context!!.integerRes(R.integer.recycler_view_grid_spacing_px),
              context!!.integerRes(R.integer.minimal_image_recycler_view_grid_size)))
      minimalFragmentRecyclerView.layoutManager = it
      minimalImageAdapter = MinimalImageAdapter(recyclerAdapterPresenter)
      minimalFragmentRecyclerView.adapter = minimalImageAdapter
      DragSelectTouchListener.create(context!!, minimalImageAdapter!!) {
        this.mode = RANGE
      }.let { touchListener ->
        minimalFragmentRecyclerView.addOnItemTouchListener(touchListener)
      }
    }
  }

  companion object {
    fun newInstance() = MinimalFragment()
  }

}