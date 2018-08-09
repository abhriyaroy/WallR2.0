package zebrostudio.wallr100.presentation.factory

class PurchaseAuthModelFactory {

  companion object {
    fun makePurchaseAuthModel(): PurchaseAuthModel {
      return PurchaseAuthModel(DataFactory.randomString(),
          DataFactory.randomString(),
          DataFactory.randomInteger())
    }
  }
}