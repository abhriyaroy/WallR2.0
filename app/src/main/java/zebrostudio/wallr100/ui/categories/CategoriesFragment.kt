package zebrostudio.wallr100.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import zebrostudio.wallr100.R
import zebrostudio.wallr100.ui.basefragment.BaseFragment
import javax.inject.Inject

class CategoriesFragment : BaseFragment(), CategoriesContract.CategoriesView {

  @Inject
  internal lateinit var presenter: CategoriesPresenterImpl

  companion object {
    val CATEGORIES_FRAGMENT_TAG = "Categories"

    fun newInstance(): CategoriesFragment {
      return CategoriesFragment()
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
    presenter.updateFragmentName(CATEGORIES_FRAGMENT_TAG)
  }

  override fun onDestroy() {
    presenter.detachView()
    super.onDestroy()
  }

}