package zebrostudio.wallr100.data

import zebrostudio.wallr100.data.network.NetworkManager
import zebrostudio.wallr100.data.purchase.PurchaseHelper

class DataRepository (
  private var purchaseHelper: PurchaseHelper,
  private var networkManager: NetworkManager
) :DataRepositoryContract {

  internal fun initPurchaseHelper() {
    purchaseHelper.init()
  }

  internal fun disposePurchaseHelper() {
    purchaseHelper.dispose()
  }

  internal fun onPurchaseClick(){
    if (networkManager.isNetworkAvailable()){

    }
    else
  }
}