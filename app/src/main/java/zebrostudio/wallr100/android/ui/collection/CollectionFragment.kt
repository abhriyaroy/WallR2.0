package zebrostudio.wallr100.android.ui.collection

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.Toolbar
import android.support.v7.widget.Toolbar.OnMenuItemClickListener
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.BaseFragment
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.presentation.collection.CollectionContract
import zebrostudio.wallr100.presentation.collection.CollectionContract.CollectionView
import javax.inject.Inject

class CollectionFragment : BaseFragment(), CollectionView, OnMenuItemClickListener {

  @Inject
  internal lateinit var presenter: CollectionContract.CollectionPresenter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return container?.inflate(inflater, R.layout.fragment_collection)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    activity?.findViewById<Toolbar>(R.id.toolbar)?.setOnMenuItemClickListener(this)
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
    TODO(
        "not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  companion object {
    fun newInstance() = CollectionFragment()
  }

}