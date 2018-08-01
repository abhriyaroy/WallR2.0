package zebrostudio.wallr100.data

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import zebrostudio.wallr100.data.factory.PurchaseAuthResponseEntityFactory
import zebrostudio.wallr100.data.mapper.ProAuthMapperImpl
import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity
import zebrostudio.wallr100.domain.model.PurchaseAuthModel

class ProAuthMapperTest {

  private lateinit var proAuthMapperImpl: ProAuthMapperImpl

  @Before
  fun setUp() {
    proAuthMapperImpl = ProAuthMapperImpl()
  }

  @Test
  fun mapFromEntityTest() {
    val purchaseAuthResponseEntity = PurchaseAuthResponseEntityFactory.makePurchaseAuthResponse()
    val purchaseAuthModel = proAuthMapperImpl.mapFromEntity(purchaseAuthResponseEntity)

    assertDataEquality(purchaseAuthResponseEntity, purchaseAuthModel)

  }

  private fun assertDataEquality(
    purchaseAuthResponseEntity: PurchaseAuthResponseEntity,
    purchaseAuthModel: PurchaseAuthModel
  ) {
    Assert.assertEquals(purchaseAuthResponseEntity.message, purchaseAuthModel.message)
    Assert.assertEquals(purchaseAuthResponseEntity.status, purchaseAuthModel.status)
    Assert.assertEquals(purchaseAuthResponseEntity.errorCode, purchaseAuthModel.errorCode)
  }

}