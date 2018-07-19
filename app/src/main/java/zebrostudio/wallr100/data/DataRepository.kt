package zebrostudio.wallr100.data

import zebrostudio.wallr100.data.purchase.PurchaseHelper

class DataRepository(private var purchaseHelper: PurchaseHelper) {

  internal fun initPurchaseHelper() {
    purchaseHelper.init()
  }

  internal fun disposePurchaseHelper(){
    purchaseHelper.dispose()
  }
}