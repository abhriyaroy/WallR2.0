package zebrostudio.wallr100.presentation.mapper

import zebrostudio.wallr100.domain.model.PurchaseAuthResponse
import zebrostudio.wallr100.presentation.entity.PurchaseAuthResponsePresentationEntity

class ProAuthPresentationMapperImpl : ProAuthPresentationMapper<PurchaseAuthResponsePresentationEntity,
    PurchaseAuthResponse> {

  override fun mapFromPresentationEntity(type: PurchaseAuthResponsePresentationEntity)
      : PurchaseAuthResponse {
    return PurchaseAuthResponse(type.status, type.message, type.errorCode)
  }

  override fun mapToPresentationEntity(type: PurchaseAuthResponse)
      : PurchaseAuthResponsePresentationEntity {
    return PurchaseAuthResponsePresentationEntity(type.status, type.message, type.errorCode)
  }

}