package zebrostudio.wallr100.ui.collection

import zebrostudio.wallr100.BasePresenter

interface CollectionContract {

  interface CollectionView

  interface CollectionPresenter : BasePresenter<CollectionView>

}