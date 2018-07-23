package zebrostudio.wallr100.android.ui.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.basefragment.BaseFragment
import zebrostudio.wallr100.android.utils.inflate
import zebrostudio.wallr100.presentation.collection.CollectionContract
import javax.inject.Inject

class CollectionFragment : BaseFragment(), CollectionContract.CollectionView {

  @Inject
  internal lateinit var presenter: CollectionContract.CollectionPresenter

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return container?.inflate(inflater, R.layout.fragment_collection)
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

  companion object {
    const val COLLECTION_FRAGMENT_TAG = "Collection"

    fun newInstance() = CollectionFragment()
  }

}