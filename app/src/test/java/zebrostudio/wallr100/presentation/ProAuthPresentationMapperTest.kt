package zebrostudio.wallr100.presentation

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import zebrostudio.wallr100.domain.model.PurchaseAuthModel
import zebrostudio.wallr100.presentation.entity.PurchaseAuthPresentationEntity
import zebrostudio.wallr100.presentation.factory.PurchaseAuthModelFactory
import zebrostudio.wallr100.presentation.mapper.ProAuthPresentationMapperImpl

class ProAuthPresentationMapperTest {

  private lateinit var proAuthPresentationMapperImpl: ProAuthPresentationMapperImpl

  @Before
  fun setUp() {
    proAuthPresentationMapperImpl = ProAuthPresentationMapperImpl()
  }

  @Test
  fun mapFromEntityTest() {
    val purchaseAuthModel = PurchaseAuthModelFactory.makePurchaseAuthModel()
    val proAuthPresentationModel =
        proAuthPresentationMapperImpl.mapToPresentationEntity(purchaseAuthModel)

    assertDataEquality(proAuthPresentationModel, purchaseAuthModel)

  }

  private fun assertDataEquality(
    purchaseAuthResponsePresentationEntity: PurchaseAuthPresentationEntity,
    purchaseAuthModel: PurchaseAuthModel
  ) {
    Assert.assertEquals(purchaseAuthResponsePresentationEntity.message, purchaseAuthModel.message)
    Assert.assertEquals(purchaseAuthResponsePresentationEntity.status, purchaseAuthModel.status)
    Assert.assertEquals(purchaseAuthResponsePresentationEntity.errorCode,
        purchaseAuthModel.errorCode)
  }

}