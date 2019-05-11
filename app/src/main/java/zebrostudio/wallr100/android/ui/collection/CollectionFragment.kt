package zebrostudio.wallr100.android.ui.collection

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
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
import android.widget.RelativeLayout
import com.afollestad.materialcab.MaterialCab
import com.afollestad.materialdialogs.MaterialDialog
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.qingmei2.rximagepicker.core.RxImagePicker
import com.qingmei2.rximagepicker_extension.MimeType
import com.qingmei2.rximagepicker_extension_zhihu.ZhihuConfigurationBuilder
import com.uber.autodispose.autoDisposable
import dagger.android.support.AndroidSupportInjection
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_main.blurView
import kotlinx.android.synthetic.main.activity_main.loadingHintBelowProgressSpinkit
import kotlinx.android.synthetic.main.activity_main.rootFrameLayout
import kotlinx.android.synthetic.main.activity_main.wallpaperActionProgressSpinkit
import kotlinx.android.synthetic.main.fragment_collection.collectionsRecyclerView
import kotlinx.android.synthetic.main.fragment_collection.imagesAbsentLayout
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.ui.adapters.CollectionsImageAdapter
import zebrostudio.wallr100.android.ui.adapters.CollectionsImageAdapterCallbacks
import zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper.CollectionRecyclerTouchHelperCallback
import zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper.OnStartDragListener
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.ui.detail.images.BLUR_RADIUS
import zebrostudio.wallr100.android.utils.RecyclerViewItemDecorator
import zebrostudio.wallr100.android.utils.colorRes
import zebrostudio.wallr100.android.utils.errorToast
import zebrostudio.wallr100.android.utils.gone
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.android.utils.infoToast
import zebrostudio.wallr100.android.utils.integerRes
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.android.utils.successToast
import zebrostudio.wallr100.android.utils.visible
import zebrostudio.wallr100.android.utils.withDelayOnMain
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionRecyclerPresenter
import zebrostudio.wallr100.presentation.collection.AUTOSTART_CLASS_NAME_OPPO
import zebrostudio.wallr100.presentation.collection.AUTOSTART_CLASS_NAME_VIVO
import zebrostudio.wallr100.presentation.collection.AUTOSTART_CLASS_NAME_XIAOMI
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionPresenter
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionView
import zebrostudio.wallr100.presentation.collection.MANUFACTURER_NAME_OPPO
import zebrostudio.wallr100.presentation.collection.MANUFACTURER_NAME_VIVO
import zebrostudio.wallr100.presentation.collection.MANUFACTURER_NAME_XIAOMI
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import zebrostudio.wallr100.presentation.collection.SECURITY_PACKAGE_OPPO
import zebrostudio.wallr100.presentation.collection.SECURITY_PACKAGE_VIVO
import zebrostudio.wallr100.presentation.collection.SECURITY_PACKAGE_XIAOMI
import javax.inject.Inject

const val REQUEST_CODE = 1
const val MAXIMUM_SELECTED_IMAGES = 10
private const val REORDER_HINT_VIEW_POSITION = 1
private const val DISABLE_APP_BAR_DELAY: Long = 400
private const val AUTOSTART_HINT_DELAY: Long = 2000

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
    setUpBlurView()
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

  override fun getManufacturerName(): String {
    return android.os.Build.MANUFACTURER
  }

  override fun onMenuItemClick(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.change_wallpaper_interval ->
        presenter.handleAutomaticWallpaperChangerIntervalMenuItemClicked()
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
    activity?.let {
      it.findViewById<Toolbar>(R.id.toolbar)?.layoutParams.let {
        (it as AppBarLayout.LayoutParams).scrollFlags =
            (AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
      }
      it.findViewById<AppBarLayout>(R.id.appbar)?.setExpanded(true, true)
    }
  }

  override fun hideAppBar() {
    activity?.let {
      it.findViewById<AppBarLayout>(R.id.appbar)?.setExpanded(false, true)
      withDelayOnMain(DISABLE_APP_BAR_DELAY, block = {
        it.findViewById<Toolbar>(R.id.toolbar)?.layoutParams.let {
          (it as AppBarLayout.LayoutParams).scrollFlags = 0
        }
      })
    }
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

  override fun showWallpaperChangerLayout() {
    activity?.findViewById<RelativeLayout>(R.id.switchLayout)?.visible()
  }

  override fun hideWallpaperChangerLayout() {
    activity?.findViewById<RelativeLayout>(R.id.switchLayout)?.gone()
  }

  override fun showAutomaticWallpaperStateAsActive() {
    activity?.findViewById<SwitchCompat>(R.id.switchView)?.isChecked = true
  }

  override fun showAutomaticWallpaperStateAsInActive() {
    activity?.findViewById<SwitchCompat>(R.id.switchView)?.isChecked = false
  }

  override fun showWallpaperChangerIntervalUpdatedSuccessMessage() {
    successToast(
        stringRes(R.string.collections_fragment_wallpaper_changer_interval_updated_success_message))
  }

  override fun showWallpaperChangerRestartedSuccessMessage() {
    successToast(stringRes(
        R.string.collections_fragment_wallpaper_changer_service_restarted_success_message))
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

  override fun blurScreen() {
    activity?.blurView?.visible()
  }

  override fun removeBlurFromScreen() {
    activity?.blurView?.gone()
  }

  override fun showIndefiniteLoaderWithMessage(message: String) {
    activity?.let {
      it.wallpaperActionProgressSpinkit.visible()
      it.loadingHintBelowProgressSpinkit.text = message
      it.loadingHintBelowProgressSpinkit.visible()
    }
  }

  override fun showSetWallpaperSuccessMessage() {
    successToast(stringRes(R.string.set_wallpaper_success_message))
  }

  override fun showCrystallizeWallpaperSuccessMessage() {
    successToast(stringRes(R.string.crystallizing_wallpaper_successful_message))
  }

  override fun showGenericErrorMessage() {
    errorToast(stringRes(R.string.generic_error_message))
  }

  override fun showAutoStartPermissionRequiredDialog() {
    MaterialDialog.Builder(activity!!)
        .title(getString(R.string.collection_fragment_autostart_permission_title))
        .content(stringRes(R.string.collection_fragment_autostart_permission_description))
        .onPositive { _, _ -> openAutoStartSettings() }
        .cancelable(true)
        .positiveText(stringRes(R.string.collection_fragment_autostart_permission_positive_text))
        .negativeText(stringRes(R.string.collection_fragment_autostart_permission_negative_text))
        .show()
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

  private fun setUpBlurView() {
    activity?.blurView?.setupWith(activity!!.rootFrameLayout)
        ?.setBlurAlgorithm(RenderScriptBlur(context!!))
        ?.setBlurRadius(BLUR_RADIUS)
  }

  private fun openAutoStartSettings() {
    Intent().apply {
      getManufacturerName().let {
        when {
          it.equals(MANUFACTURER_NAME_XIAOMI, true) ->
            component = ComponentName(SECURITY_PACKAGE_XIAOMI, AUTOSTART_CLASS_NAME_XIAOMI)
          it.equals(MANUFACTURER_NAME_OPPO, true) ->
            component = ComponentName(SECURITY_PACKAGE_OPPO, AUTOSTART_CLASS_NAME_OPPO)
          it.equals(MANUFACTURER_NAME_VIVO, true) ->
            component = ComponentName(SECURITY_PACKAGE_VIVO,
                AUTOSTART_CLASS_NAME_VIVO)
        }
      }
    }.let {
      val list = context!!.packageManager
          .queryIntentActivities(it, PackageManager.MATCH_DEFAULT_ONLY)
      if (list.isNotEmpty()) {
        withDelayOnMain(AUTOSTART_HINT_DELAY) {
          infoToast(stringRes(R.string.collections_fragment_enable_autostart_settings_hint))
        }
        startActivity(it)
      }
    }
  }

  companion object {
    fun newInstance() = CollectionFragment()
  }

}