package zebrostudio.wallr100.presentation.collection

import zebrostudio.wallr100.presentation.BasePresenter

interface CollectionContract {

  interface CollectionView

  interface CollectionPresenter : BasePresenter<CollectionView>

}