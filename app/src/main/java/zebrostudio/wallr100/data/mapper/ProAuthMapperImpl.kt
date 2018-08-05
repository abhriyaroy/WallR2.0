package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity
import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class ProAuthMapperImpl : ProAuthMapper<PurchaseAuthResponseEntity, PurchaseAuthModel> {

  override fun mapFromEntity(type: PurchaseAuthResponseEntity): PurchaseAuthModel {
    if (type.status == "success"){
      return PurchaseAuthModel(type.status, "verified", 200)
    }
    return PurchaseAuthModel(type.status, type.message, type.errorCode)
  }

}