package zebrostudio.wallr100.ui.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zebrostudio.wallr100.R
import zebrostudio.wallr100.ui.basefragment.BaseFragment
import zebrostudio.wallr100.utils.inflate
import javax.inject.Inject

@Suppress("NOTHING_TO_INLINE")
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

    inline fun newInstance(): CollectionFragment {
      return CollectionFragment()
    }
  }

}