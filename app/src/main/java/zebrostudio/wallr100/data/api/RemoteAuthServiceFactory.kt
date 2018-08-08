package zebrostudio.wallr100.data.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RemoteAuthServiceFactory {

  private var retrofit: Retrofit? = null

  fun verifyPurchaseService(): FirebaseAuthService {
    if (retrofit == null) {
      retrofit = Retrofit.Builder()
          .baseUrl(UrlMap.FIREBASE_PURCHASE_AUTH_URL)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .build()
    }
    return retrofit!!.create(FirebaseAuthService::class.java)
  }

}