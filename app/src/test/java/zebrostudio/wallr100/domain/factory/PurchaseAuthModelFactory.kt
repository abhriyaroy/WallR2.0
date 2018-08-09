package zebrostudio.wallr100.domain.factory

class PurchaseAuthModelFactory {

  companion object {
    fun makePurchaseAuthResponse(): PurchaseAuthModel {
      return PurchaseAuthModel(DataFactory.randomString(),
          DataFactory.randomString(),
          DataFactory.randomInteger())
    }
  }
}