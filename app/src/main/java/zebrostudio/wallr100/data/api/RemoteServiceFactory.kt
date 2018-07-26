package zebrostudio.wallr100.data.api

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RemoteServiceFactory {

  private var retrofit: Retrofit = Retrofit.Builder()
      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build()

  fun verifyPurchaseService(): FirebaseAuthService? =
      retrofit.create(FirebaseAuthService::class.java)

}