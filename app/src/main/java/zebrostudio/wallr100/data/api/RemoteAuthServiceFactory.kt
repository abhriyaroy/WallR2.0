package zebrostudio.wallr100.data.api

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity

interface RemoteAuthServiceFactory {
  fun verifyPurchaseService(url: String): Single<PurchaseAuthResponseEntity>
}

class RemoteAuthServiceFactoryImpl : RemoteAuthServiceFactory {

  private var retrofit: Retrofit? = null

  override fun verifyPurchaseService(url: String): Single<PurchaseAuthResponseEntity> {
    if (retrofit == null) {
      retrofit = Retrofit.Builder()
          .baseUrl(UrlMap.FIREBASE_PURCHASE_AUTH_URL)
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .addConverterFactory(GsonConverterFactory.create())
          .build()
    }
    return retrofit!!.create(FirebaseAuthService::class.java).verifyPurchase(url)
  }

}