package zebrostudio.wallr100.android.ui.collection

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView.ViewHolder
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.support.v7.widget.Toolbar.OnMenuItemClickListener
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.afollestad.materialcab.MaterialCab
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_collection.collectionsRecyclerView
import kotlinx.android.synthetic.main.fragment_collection.imagesAbsentLayout
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.ui.adapters.CollectionsImageAdapter
import zebrostudio.wallr100.android.ui.adapters.CollectionsImageAdapterCallbacks
import zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper.CollectionRecyclerTouchHelperCallback
import zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper.OnStartDragListener
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.utils.RecyclerViewItemDecorator
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionPresenter
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionView
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import javax.inject.Inject

const val REQUEST_CODE = 1

class CollectionFragment : BaseFragment(),
    CollectionView,
    OnMenuItemClickListener,
    OnStartDragListener,
    CollectionsImageAdapterCallbacks {

  @Inject internal lateinit var presenter: CollectionPresenter
  @Inject internal lateinit var recyclerPresenter: CollectionRecyclerPresenter

  private lateinit var collectionRecyclerTouchHelperCallback: CollectionRecyclerTouchHelperCallback
  private lateinit var itemTouchHelper: ItemTouchHelper
  private lateinit var collectionsImageAdapter: CollectionsImageAdapter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    AndroidSupportInjection.inject(this)
    return container?.inflate(inflater, R.layout.fragment_collection)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    presenter.attachView(this)
    activity?.findViewById<Toolbar>(R.id.toolbar)?.setOnMenuItemClickListener(this)
    initRecyclerViewWithListeners()
    attachAutomaticWallpaperChangerListener()
    presenter.handleViewCreated()
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)
    setHasOptionsMenu(true)
  }

  @SuppressLint("RestrictedApi")
  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    activity!!.menuInflater.inflate(R.menu.collection, menu)
    if (menu is MenuBuilder) {
      menu.setOptionalIconsVisible(true)
    }
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  override fun onMenuItemClick(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.change_wallpaper_interval -> presenter.handleChangeWallpaperIntervalClicked()
      else -> presenter.handleImportFromLocalStorageClicked()
    }
    return true
  }

  override fun onStartDrag(viewHolder: ViewHolder) {
    if (MaterialCab.isActive) {
      MaterialCab.destroy()
    }
    itemTouchHelper.startDrag(viewHolder)
  }

  override fun onItemMoved(fromPosition: Int, toPosition: Int) {
    presenter.handleItemMoved(fromPosition, toPosition, collectionsImageAdapter.getImagePathList())
  }

  override fun handleClick(index: Int) {
    presenter.handleItemClicked(index, collectionsImageAdapter.getImagePathList(),
        collectionsImageAdapter.getSelectedItemsMap())
  }

  override fun handleLongClick(index: Int): Boolean {
    presenter.handleItemLongClicked(index, collectionsImageAdapter.getImagePathList(),
        collectionsImageAdapter.getSelectedItemsMap())
    return true
  }

  override fun showPurchasePremiumToContinueDialog() {

  }

  override fun redirectToBuyPro() {
    startActivityForResult(Intent(context, BuyProActivity::class.java), REQUEST_CODE)
  }

  override fun hasStoragePermission(): Boolean {
    context!!.let {
      val readPermission =
          ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE)
      val writePermission =
          ContextCompat.checkSelfPermission(it, Manifest.permission.WRITE_EXTERNAL_STORAGE)
      if (readPermission != PackageManager.PERMISSION_GRANTED ||
          writePermission != PackageManager.PERMISSION_GRANTED) {
        return false
      }
      return true
    }
  }

  override fun requestStoragePermission() {
    requestPermissions(activity!!, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE)
  }

  override fun showImageOptionsHint() {

  }

  override fun showReorderImagesHint() {

  }

  override fun showImages(imageList: List<CollectionsPresenterEntity>) {
    collectionsImageAdapter.setColorList(imageList)
  }

  override fun hideImagesAbsentLayout() {
    imagesAbsentLayout.gone()
  }

  override fun showImagesAbsentLayout() {
    imagesAbsentLayout.visible()
  }

  override fun hideAutomaticWallpaperChangerLayout() {
    activity?.findViewById<RelativeLayout>(R.id.switchLayout)?.gone()
  }

  override fun showAutomaticWallpaperChangerLayout() {
    activity?.findViewById<RelativeLayout>(R.id.switchLayout)?.visible()
  }

  override fun showAutomaticWallpaperStateAsActive() {
    activity?.findViewById<SwitchCompat>(R.id.switchView)?.isChecked = true
  }

  override fun showAutomaticWallpaperStateAsInActive() {
    activity?.findViewById<SwitchCompat>(R.id.switchView)?.isChecked = false
  }

  override fun showGenericErrorMessage() {
    errorToast(stringRes(R.string.generic_error_message))
  }

  private fun initRecyclerViewWithListeners() {
    collectionsImageAdapter = CollectionsImageAdapter(this, recyclerPresenter)
    collectionRecyclerTouchHelperCallback =
        CollectionRecyclerTouchHelperCallback(collectionsImageAdapter)
    itemTouchHelper = ItemTouchHelper(collectionRecyclerTouchHelperCallback)
    collectionsRecyclerView?.apply {
      GridLayoutManager(context,
          integerRes(R.integer.recycler_view_span_count)).let {
        layoutManager = it
      }
      addItemDecoration(
          RecyclerViewItemDecorator(integerRes(R.integer.recycler_view_grid_spacing_px),
              integerRes(R.integer.minimal_image_recycler_view_grid_size)))
      adapter = collectionsImageAdapter
      itemTouchHelper.attachToRecyclerView(this)
    }
  }

  private fun attachAutomaticWallpaperChangerListener() {
    activity?.findViewById<SwitchCompat>(R.id.switchView)
        ?.setOnCheckedChangeListener { _, isChecked ->
          if (isChecked) {
            presenter.handleAutomaticWallpaperChangerEnabled()
          } else {
            presenter.handleAutomaticWallpaperChangerDisabled()
          }
        }
  }

  companion object {
    fun newInstance() = CollectionFragment()
  }

}