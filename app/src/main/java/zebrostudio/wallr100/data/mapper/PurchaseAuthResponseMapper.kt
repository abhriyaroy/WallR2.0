package zebrostudio.wallr100.data.mapper

import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity
import zebrostudio.wallr100.domain.model.PurchaseAuthResponse

class PurchaseAuthResponseMapper : Mapper<PurchaseAuthResponseEntity, PurchaseAuthResponse> {

  override fun mapFromEntity(type: PurchaseAuthResponseEntity): PurchaseAuthResponse {
    return PurchaseAuthResponse(type.status, type.message, type.errorCode)
  }

}