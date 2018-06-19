package zebrostudio.wallr100.ui.collection

import zebrostudio.wallr100.ui.basefragment.FragmentBasePresenter

interface CollectionContract{

  interface CollectionView

  interface CollectionPresenter : FragmentBasePresenter<CollectionView>

}