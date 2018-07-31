package zebrostudio.wallr100.data.api

class UrlMap {

  companion object {
    const val firebasePurchaseAuthUrl = "https://us-central1-wallrproduction.cloudfunctions.net/"

    fun getFirebasePurchaseAuthEndpoint(
      packageName: String,
      skuId: String,
      purchaseToken: String
    ) = "purchaseVerification?packageName=$packageName&skuId=$skuId&purchaseToken=$purchaseToken"
  }

}