package zebrostudio.wallr100.data.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url
import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity

interface FirebaseAuthService {

  @GET("https://us-central1-wallrproduction.cloudfunctions.net/")
  fun verifyPurchase(@Url url: String): Single<PurchaseAuthResponseEntity>

}