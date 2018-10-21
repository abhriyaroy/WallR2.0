package zebrostudio.wallr100.data

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.api.UrlMap
import java.util.UUID.*

@RunWith(MockitoJUnitRunner::class)
class UrlMapTest {

  private val packageName = randomUUID().toString()
  private val skuId = randomUUID().toString()
  private val purchaseToken = randomUUID().toString()
  private val keyword = randomUUID().toString()
  private val queryPage = 1
  private val firebasePurchaseAuthUrl = "https://us-central1-wallrproduction.cloudfunctions.net/"
  private val firebasePurchaseAuthEndpoint =
      "purchaseVerification?packageName=$packageName&skuId=$skuId&purchaseToken=$purchaseToken"
  private val queryString = "photos/search?query=$keyword&per_page=30&page=$queryPage"

  @Test
  fun `should return valid firebase purchase auth base url on firebase_puchase_auth_url call`() {
    assertEquals(firebasePurchaseAuthUrl, UrlMap.FIREBASE_PURCHASE_AUTH_URL)
  }

  @Test
  fun `should return valid firebase purchase auth endpoint on getFirebasePurchaseAuthEndpoint call`() {
    assertEquals(firebasePurchaseAuthEndpoint,
        UrlMap.getFirebasePurchaseAuthEndpoint(packageName, skuId, purchaseToken))
  }

  @Test fun `should return valid search query string on getQueryString call`() {
    assertEquals(queryString, UrlMap.getQueryString(keyword, queryPage))
  }

}