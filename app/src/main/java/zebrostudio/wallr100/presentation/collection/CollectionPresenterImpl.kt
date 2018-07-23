package zebrostudio.wallr100.presentation.collection

class CollectionPresenterImpl : CollectionContract.CollectionPresenter {

  private var collectionView: CollectionContract.CollectionView? = null

  override fun attachView(view: CollectionContract.CollectionView) {
    collectionView = view
  }

  override fun detachView() {
    collectionView = null
  }

}