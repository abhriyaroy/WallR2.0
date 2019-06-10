package zebrostudio.wallr100.presentation.mappertests

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.model.RestoreColorsModel
import zebrostudio.wallr100.presentation.minimal.mapper.RestoreColorsPresenterEntityMapper
import zebrostudio.wallr100.presentation.minimal.model.RestoreColorsPresenterEntity
import java.util.TreeMap
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class RestoreColorsPresenterEntityMapperTest {

  private lateinit var restoreColorsPresenterEntityMapper: RestoreColorsPresenterEntityMapper

  @Before fun setup() {
    restoreColorsPresenterEntityMapper = RestoreColorsPresenterEntityMapper()
  }

  @Test fun `should return restoreColorsPresenterEntity on mapToPresenterEntity call success`() {
    val colorsList = listOf(randomUUID().toString(), randomUUID().toString())
    val selectedItemsMap = TreeMap<Int, String>()
    selectedItemsMap[0] = randomUUID().toString()
    val restoreColorsModel = RestoreColorsModel(colorsList, selectedItemsMap)
    val restoreColorsPresenterEntity = RestoreColorsPresenterEntity(colorsList, selectedItemsMap)

    val result = restoreColorsPresenterEntityMapper.mapToPresenterEntity(restoreColorsModel)

    assertEquals(restoreColorsPresenterEntity, result)
  }
}