package zebrostudio.wallr100.ui.categories

import zebrostudio.wallr100.ui.basefragment.BaseFragment
import javax.inject.Inject

class CategoriesFragment : BaseFragment(), CategoriesContract.CaategoriesView {

  @Inject
  internal lateinit var presenter: CategoriesPresenterImpl

  companion object {
    val CATEGORIES_FRAGMENT_TAG = "Categories"

    fun newInstance(): CategoriesFragment {
      return CategoriesFragment()
    }
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