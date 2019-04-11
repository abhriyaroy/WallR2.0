package zebrostudio.wallr100.data.api

import zebrostudio.wallr100.BuildConfig

object UrlMap {
  const val FIREBASE_PURCHASE_AUTH_URL = "https://us-central1-wallrproduction.cloudfunctions.net/"
  const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"
  const val PRODUCTION_FIREBASE_DYNAMIC_LINK_PREFIX = "https://zebrostudio.page.link"
  const val DEBUG_FIREBASE_DYNAMIC_LINK_PREFIX = "https://wallr100.page.link"

  fun getFirebasePurchaseAuthEndpoint(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ) = "purchaseVerification?packageName=$packageName&skuId=$skuId&purchaseToken=$purchaseToken"

  fun getQueryString(
    keyword: String,
    queryPage: Int
  ) = "photos/search?query=$keyword&per_page=30&page=$queryPage"

  fun getDynamicLinkPrefixUri(): String {
    return if (BuildConfig.DEBUG) {
      DEBUG_FIREBASE_DYNAMIC_LINK_PREFIX
    } else {
      PRODUCTION_FIREBASE_DYNAMIC_LINK_PREFIX
    }
  }

}
