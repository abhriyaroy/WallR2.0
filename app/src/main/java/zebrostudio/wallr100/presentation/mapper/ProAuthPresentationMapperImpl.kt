package zebrostudio.wallr100.presentation.mapper

import zebrostudio.wallr100.domain.model.PurchaseAuthModel
import zebrostudio.wallr100.presentation.entity.PurchaseAuthResponsePresentationEntity

class ProAuthPresentationMapperImpl : ProAuthPresentationMapper<PurchaseAuthResponsePresentationEntity,
    PurchaseAuthModel> {

  override fun mapToPresentationEntity(type: PurchaseAuthModel)
      : PurchaseAuthResponsePresentationEntity {
    return PurchaseAuthResponsePresentationEntity(type.status, type.message, type.errorCode)
  }

}