package zebrostudio.wallr100.ui.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zebrostudio.wallr100.R
import zebrostudio.wallr100.ui.basefragment.BaseFragment
import javax.inject.Inject

class CollectionFragment : BaseFragment(), CollectionContract.CollectionView {

  @Inject
  internal lateinit var presenter: CollectionPresenterImpl

  companion object {
    val COLLECTION_FRAGMENT_TAG = "Collection"

    fun newInstance(): CollectionFragment {
      return CollectionFragment()
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_layout, container, false)
  }

  override fun onResume() {
    super.onResume()
    presenter.attachView(this)
    presenter.updateFragmentName(COLLECTION_FRAGMENT_TAG)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

}