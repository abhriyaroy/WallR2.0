package zebrostudio.wallr100.data

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.api.UrlMap

@RunWith(MockitoJUnitRunner::class)
class UrlMapTest {

  private var packageName = java.util.UUID.randomUUID().toString()
  private var skuId = java.util.UUID.randomUUID().toString()
  private var purchaseToken = java.util.UUID.randomUUID().toString()

  @Test fun `should return valid firebase purchase auth base url`() {
    assertEquals("https://us-central1-wallrproduction.cloudfunctions.net/",
        UrlMap.FIREBASE_PURCHASE_AUTH_URL)
  }

  @Test fun `should return valid firebase purchase auth endpoint`() {
    assertEquals(
        "purchaseVerification?packageName=$packageName&skuId=$skuId&purchaseToken=$purchaseToken",
        UrlMap.getFirebasePurchaseAuthEndpoint(packageName, skuId, purchaseToken))
  }

}