package zebrostudio.wallr100.android.ui.collection

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.content.ContextCompat
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.Toolbar
import android.support.v7.widget.Toolbar.OnMenuItemClickListener
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import dagger.android.support.AndroidSupportInjection
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.ui.buypro.BuyProActivity
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.presentation.collection.CollectionContract
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionView
import javax.inject.Inject

const val REQUEST_CODE = 1

class CollectionFragment : BaseFragment(), CollectionView, OnMenuItemClickListener {

  @Inject
  internal lateinit var presenter: CollectionContract.CollectionPresenter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    AndroidSupportInjection.inject(this)
    return container?.inflate(inflater, R.layout.fragment_collection)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    activity?.findViewById<Toolbar>(R.id.toolbar)?.setOnMenuItemClickListener(this)
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

  companion object {
    fun newInstance() = CollectionFragment()
  }

}