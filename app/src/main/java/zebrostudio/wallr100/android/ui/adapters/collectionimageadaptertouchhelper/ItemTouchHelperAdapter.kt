package zebrostudio.wallr100.android.ui.adapters.collectionimageadaptertouchhelper

interface ItemTouchHelperAdapter {
  fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
}