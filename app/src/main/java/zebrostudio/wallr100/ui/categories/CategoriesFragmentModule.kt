package zebrostudio.wallr100.ui.categories

import dagger.Module
import dagger.Provides
import zebrostudio.wallr100.data.DataRepository

@Module
class CategoriesFragmentModule {

  @Provides
  internal fun provideCategoriesPresenter(dataRepository: DataRepository)
      : CategoriesContract.CategoriesPresenter = CategoriesPresenterImpl(dataRepository)

}