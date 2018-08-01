package zebrostudio.wallr100.presentation.mapper

import zebrostudio.wallr100.domain.model.PurchaseAuthModel
import zebrostudio.wallr100.presentation.entity.PurchaseAuthPresentationEntity

class ProAuthPresentationMapperImpl : ProAuthPresentationMapper<PurchaseAuthPresentationEntity,
    PurchaseAuthModel> {

  override fun mapToPresentationEntity(type: PurchaseAuthModel)
      : PurchaseAuthPresentationEntity {
    return PurchaseAuthPresentationEntity(type.status, type.message, type.errorCode)
  }

}