package zebrostudio.wallr100.data

import android.content.Context
import android.net.ConnectivityManager
import io.reactivex.Single
import zebrostudio.wallr100.data.api.RemoteServiceFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.mapper.ProAuthMapperImpl
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class WallrDataRepository(
  private var context: Context,
  private var remoteServiceFactory: RemoteServiceFactory,
  private var mapperImpl: ProAuthMapperImpl
) : WallrRepository {

  override fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Single<PurchaseAuthModel> {

    return remoteServiceFactory.verifyPurchaseService()
        .verifyPurchase(UrlMap.getFirebasePurchaseAuthEndpoint(packageName, skuId, purchaseToken))
        .map {
          mapperImpl.mapFromEntity(it)
        }

  }

  override fun isInternetAvailable(): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    val activeNetworkInfo = connectivityManager!!.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
  }

}