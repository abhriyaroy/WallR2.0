package zebrostudio.wallr100.data.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url
import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity

interface FirebaseAuthService {

  @GET()
  fun verifyPurchase(@Url url: String): Single<PurchaseAuthResponseEntity>

}