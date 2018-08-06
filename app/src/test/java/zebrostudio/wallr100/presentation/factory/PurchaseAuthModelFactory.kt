package zebrostudio.wallr100.presentation.factory

import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class PurchaseAuthModelFactory {

  companion object {
    fun makePurchaseAuthModel(): PurchaseAuthModel {
      return PurchaseAuthModel(DataFactory.randomString(),
          DataFactory.randomString(),
          DataFactory.randomInteger())
    }
  }
}