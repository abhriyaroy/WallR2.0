package zebrostudio.wallr100.data

interface  DataRepositoryContract {

  fun initPurchaseHelper()
  fun disposePurchaseHelper()
  fun onPurchaseClick()
  fun onRestoreClick()
}