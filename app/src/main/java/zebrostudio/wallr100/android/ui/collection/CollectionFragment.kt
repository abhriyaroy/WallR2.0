package zebrostudio.wallr100.android.ui.collection

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
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
import com.afollestad.materialcab.MaterialCab
import com.afollestad.materialdialogs.MaterialDialog
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.qingmei2.rximagepicker.core.RxImagePicker
import com.qingmei2.rximagepicker_extension.MimeType
import com.qingmei2.rximagepicker_extension_zhihu.ZhihuConfigurationBuilder
import com.uber.autodispose.autoDisposable
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
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.android.utils.showAnimation
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.successToast
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionPresenter
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionView
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import javax.inject.Inject

const val REQUEST_CODE = 1
const val MAXIMUM_SELECTED_IMAGES = 10
private const val REORDER_HINT_VIEW_POSITION = 1

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
    showAutomaticWallpaperStateAsInActive()
    attachToolbarCollapseListener()
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

  override fun onMenuItemClick(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.change_wallpaper_interval -> presenter.handleChangeWallpaperIntervalClicked()
      else -> presenter.handleImportFromLocalStorageClicked()
    }
    return true
  }

  override fun onStartDrag(viewHolder: ViewHolder) {
    presenter.notifyDragStarted()
    itemTouchHelper.startDrag(viewHolder)
  }

  override fun handleItemMoved(fromPosition: Int, toPosition: Int) {
    presenter.handleItemMoved(fromPosition, toPosition, collectionsImageAdapter.getImagePathList())
  }

  override fun handleClick(index: Int) {
    presenter.handleItemClicked(index, collectionsImageAdapter.getImagePathList(),
        collectionsImageAdapter.getSelectedItemsMap())
  }

  override fun showAppBar() {
    activity?.findViewById<AppBarLayout>(R.id.appbar)?.setExpanded(true, true)
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

  override fun showReorderImagesHint() {
    val targetView = collectionsRecyclerView.getChildAt(REORDER_HINT_VIEW_POSITION)
        .findViewById<View>(R.id.hintStubView)
    TapTargetView.showFor(activity!!,
        TapTarget.forView(targetView,
            getString(R.string.collections_fragment_drag_to_reorder_hint_title),
            getString(R.string.collections_fragment_drag_to_reorder_hint_description))
            .dimColor(android.R.color.transparent)
            .outerCircleColor(R.color.accent)
            .targetCircleColor(R.color.concrete)
            .transparentTarget(true)
            .textColor(android.R.color.white)
            .cancelable(true),
        object : TapTargetView.Listener() {
          override fun onTargetClick(view: TapTargetView) {
            super.onTargetClick(view)
            presenter.handleReorderImagesHintHintDismissed()
          }

          override fun onTargetDismissed(view: TapTargetView, userInitiated: Boolean) {
            presenter.handleReorderImagesHintHintDismissed()
          }

          override fun onOuterCircleClick(view: TapTargetView) {
            view.dismiss(true)
          }
        }
    )
  }

  override fun setImagesList(imageList: List<CollectionsPresenterEntity>) {
    collectionsImageAdapter.setImagesList(imageList)
  }

  override fun clearImages() {
    collectionsImageAdapter.clearImagesList()
  }

  override fun hideImagesAbsentLayout() {
    imagesAbsentLayout.gone()
  }

  override fun showImagesAbsentLayout() {
    imagesAbsentLayout.visible()
  }

  override fun showAutomaticWallpaperStateAsActive() {
    activity?.findViewById<SwitchCompat>(R.id.switchView)?.isChecked = true
  }

  override fun showAutomaticWallpaperStateAsInActive() {
    activity?.findViewById<SwitchCompat>(R.id.switchView)?.isChecked = false
  }

  override fun showWallpaperChangerIntervalDialog(choice: Int) {
    MaterialDialog.Builder(activity!!)
        .backgroundColor(colorRes(R.color.primary))
        .title(getString(R.string.collections_fragment_wallpaper_changer_diloag_title))
        .items(R.array.wallpaperChangerIntervals)
        .contentColor(colorRes(R.color.white))
        .widgetColor(colorRes(R.color.accent))
        .positiveColor(colorRes(R.color.accent))
        .itemsCallbackSingleChoice(choice) { _, _, which, _ ->
          presenter.updateWallpaperChangerInterval(which)
          true
        }
        .positiveText(
            stringRes(R.string.collections_fragment_wallpaper_changer_dilog_positive_text))
        .show()
  }

  override fun showImagePicker() {
    mutableListOf<Uri>().let { list ->
      RxImagePicker
          .create(ImagePickerHelper::class.java)
          .fromGallery(context!!, ZhihuConfigurationBuilder(MimeType.ofImage(), false)
              .maxSelectable(MAXIMUM_SELECTED_IMAGES)
              .countable(true)
              .theme(R.style.Zhihu_Normal)
              .build())
          .doOnComplete {
            presenter.handleImagePickerResult(list)
          }
          .autoDisposable(getScope())
          .subscribe {
            list.add(it.uri)
          }
    }
  }

  override fun showSingleImageAddedSuccessfullyMessage() {
    successToast(stringRes(R.string.collection_fragment_add_single_image_success_message))
  }

  override fun showMultipleImagesAddedSuccessfullyMessage(count: Int) {
    successToast(
        stringRes(R.string.collection_fragment_add_multiple_image_success_message, count))
  }

  override fun removeItemView(position: Int) {
    collectionsImageAdapter.notifyItemRemoved(position)
  }

  override fun updateChangesInEveryItemView() {
    collectionsImageAdapter.notifyDataSetChanged()
  }

  override fun updateChangesInSingleItemView(position: Int) {
    collectionsImageAdapter.notifyItemChanged(position)
  }

  override fun updateItemViewMovement(fromPosition: Int, toPosition: Int) {
    collectionsImageAdapter.notifyItemMoved(fromPosition, toPosition)
  }

  override fun clearAllSelectedItems() {
    collectionsImageAdapter.clearSelectedItemsMap()
  }

  override fun isCabActive(): Boolean {
    return MaterialCab.isActive
  }

  override fun showSingleImageSelectedCab() {
    MaterialCab.attach(activity as AppCompatActivity, R.id.cabStub) {
      menuRes = R.menu.collection_single_selected
      closeDrawableRes = R.drawable.ic_close_white
      titleColor = colorRes(R.color.white)
      title = stringRes(R.string.minimal_fragment_cab_title,
          collectionsImageAdapter.getSelectedItemsMap().size)
      backgroundColor = colorRes(R.color.primary)
      backgroundColorRes(R.color.primary)

      onSelection {
        when (it.itemId) {
          R.id.setWallpaperMenuItem -> presenter.handleSetWallpaperMenuItemClicked(
              collectionsImageAdapter.getSelectedItemsMap())
          R.id.crystallizeWallpaperMenuItem -> presenter.handleCrystallizeWallpaperMenuItemClicked(
              collectionsImageAdapter.getSelectedItemsMap())
          R.id.deleteWallpaperMenuItem -> presenter.handleDeleteWallpaperMenuItemClicked(
              collectionsImageAdapter.getImagePathList(),
              collectionsImageAdapter.getSelectedItemsMap()
          )
        }
        true
      }

      onDestroy {
        presenter.handleCabDestroyed()
        true
      }
    }
  }

  override fun showMultipleImagesSelectedCab() {
    MaterialCab.attach(activity as AppCompatActivity, R.id.cabStub) {
      menuRes = R.menu.collection_multiple_selected
      closeDrawableRes = R.drawable.ic_close_white
      titleColor = colorRes(R.color.white)
      title = stringRes(R.string.minimal_fragment_cab_title,
          collectionsImageAdapter.getSelectedItemsMap().size)
      backgroundColor = colorRes(R.color.primary)
      backgroundColorRes(R.color.primary)

      onSelection {
        if (it.itemId == R.id.deleteWallpaperMenuItem) {
          presenter.handleDeleteWallpaperMenuItemClicked(
              collectionsImageAdapter.getImagePathList(),
              collectionsImageAdapter.getSelectedItemsMap()
          )
        }
        true
      }

      onDestroy {
        presenter.handleCabDestroyed()
        true
      }
    }
  }

  override fun hideCab() {
    MaterialCab.destroy()
  }

  override fun showReorderSuccessMessage() {
    successToast(stringRes(R.string.collections_fragment_image_reordering_success_message))
  }

  override fun showUnableToReorderErrorMessage() {
    errorToast(stringRes(R.string.collections_fragment_unable_to_reorder_images_error_message))
  }

  override fun showSingleImageDeleteSuccessMessage() {
    successToast(stringRes(R.string.collections_fragment_single_images_deleted_success_message))
  }

  override fun showMultipleImageDeleteSuccessMessage(count: Int) {
    successToast(stringRes(R.string.collections_fragment_multiple_images_deleted_success_message))
  }

  override fun showUnableToDeleteErrorMessage() {
    errorToast(stringRes(R.string.collections_fragment_unable_to_delete_images_error_message))
  }

  override fun showGenericErrorMessage() {
    errorToast(stringRes(R.string.generic_error_message))
  }

  private fun initRecyclerViewWithListeners() {
    collectionsImageAdapter = CollectionsImageAdapter(
        this, this, recyclerPresenter)
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
              integerRes(R.integer.recycler_view_grid_size)))
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

  private fun attachToolbarCollapseListener() {

  }

  companion object {
    fun newInstance() = CollectionFragment()
  }

}