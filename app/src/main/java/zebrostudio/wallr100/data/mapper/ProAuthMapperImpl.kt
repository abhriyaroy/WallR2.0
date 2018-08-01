package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity
import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class ProAuthMapperImpl : ProAuthMapper<PurchaseAuthResponseEntity, PurchaseAuthModel> {

  override fun mapFromEntity(type: PurchaseAuthResponseEntity): PurchaseAuthModel {
    return PurchaseAuthModel(type.status, type.message, type.errorCode)
  }

}