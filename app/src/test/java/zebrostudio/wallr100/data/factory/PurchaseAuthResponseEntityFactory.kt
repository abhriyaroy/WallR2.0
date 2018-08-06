package zebrostudio.wallr100.data.factory

import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity

class PurchaseAuthResponseEntityFactory {

  companion object {
    fun makePurchaseAuthResponse(): PurchaseAuthResponseEntity {
      return PurchaseAuthResponseEntity(DataFactory.randomString(),
          DataFactory.randomInteger(),
          DataFactory.randomString())
    }
  }
}