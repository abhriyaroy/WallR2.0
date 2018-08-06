package zebrostudio.wallr100.data.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RemoteServiceFactory {

  fun verifyPurchaseService(): FirebaseAuthService {
    return Retrofit.Builder()
        .baseUrl(UrlMap.FIREBASE_PURCHASE_AUTH_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FirebaseAuthService::class.java)
  }

}