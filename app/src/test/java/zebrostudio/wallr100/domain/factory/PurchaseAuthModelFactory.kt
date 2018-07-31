package zebrostudio.wallr100.domain.factory

import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class PurchaseAuthModelFactory {

  companion object {
    fun makePurchaseAuthResponse(): PurchaseAuthModel {
      return PurchaseAuthModel(DataFactory.randomString(),
          DataFactory.randomString(),
          DataFactory.randomInteger())
    }

  }

}