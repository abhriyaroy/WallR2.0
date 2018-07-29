package zebrostudio.wallr100.domain

import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class PurchaseAuthResponseFactory {

  companion object {

    fun makePurchaseAuthResponse(): PurchaseAuthModel {
      return PurchaseAuthModel(DataFactory.randomString(),
          DataFactory.randomString(),
          DataFactory.randomInteger())
    }

  }

}