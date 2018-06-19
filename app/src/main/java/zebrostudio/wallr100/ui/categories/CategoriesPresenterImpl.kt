package zebrostudio.wallr100.ui.categories

import zebrostudio.wallr100.data.DataRepository
import javax.inject.Inject

class CategoriesPresenterImpl
@Inject constructor(private var dataRepository: DataRepository) : CategoriesContract.CategoriesPresenter {

  private var categoriesView: CategoriesContract.CategoriesView? = null

  override fun attachView(view: CategoriesContract.CategoriesView) {
    categoriesView = view
  }

  override fun detachView() {
    categoriesView = null
  }

  override fun updateFragmentName(name: String) {
    dataRepository.updateCurrentFragmentName(name)
  }

}