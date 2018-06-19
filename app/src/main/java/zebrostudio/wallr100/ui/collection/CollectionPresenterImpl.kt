package zebrostudio.wallr100.ui.collection

import zebrostudio.wallr100.data.DataRepository
import javax.inject.Inject

class CollectionPresenterImpl
@Inject constructor(private var dataRepository: DataRepository) : CollectionContract.CollectionPresenter {

  private var collectionView: CollectionContract.CollectionView? = null

  override fun attachView(view: CollectionContract.CollectionView) {
    collectionView = view
  }

  override fun detachView() {
    collectionView = null
  }

  override fun updateFragmentName(name: String) {
    dataRepository.updateCurrentFragmentName(name)
  }
}