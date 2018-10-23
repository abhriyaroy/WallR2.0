package zebrostudio.wallr100.data.api

object UrlMap {
  const val FIREBASE_PURCHASE_AUTH_URL = "https://us-central1-wallrproduction.cloudfunctions.net/"

  const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"

  fun getFirebasePurchaseAuthEndpoint(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ) = "purchaseVerification?packageName=$packageName&skuId=$skuId&purchaseToken=$purchaseToken"

  fun getQueryString(
    keyword: String,
    queryPage: Int
  ) = "photos/search?query=$keyword&per_page=30&page=$queryPage"

}
