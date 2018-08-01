package zebrostudio.wallr100.data

import io.reactivex.Single
import zebrostudio.wallr100.data.api.RemoteServiceFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.mapper.ProAuthMapperImpl
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class WallrDataRepository(
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

}