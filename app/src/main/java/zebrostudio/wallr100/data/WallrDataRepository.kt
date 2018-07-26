package zebrostudio.wallr100.data

import io.reactivex.Single
import zebrostudio.wallr100.data.api.RemoteServiceFactory
import zebrostudio.wallr100.data.mapper.PurchaseAuthResponseMapper
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.PurchaseAuthResponse

class WallrDataRepository(
  private var remoteServiceFactory: RemoteServiceFactory,
  private var purchaseAuthResponseMapper: PurchaseAuthResponseMapper
) : WallrRepository {

  override fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  )
      : Single<PurchaseAuthResponse>? {

    val urlEndpoint =
        "purchaseVerification?packageName=$packageName&skuId=$skuId&purchaseToken=$purchaseToken"
    // Return after mapping
    return remoteServiceFactory.verifyPurchaseService()?.verifyPurchase(urlEndpoint)
        ?.map { it ->
          purchaseAuthResponseMapper.mapFromEntity(it)
        }

  }

}