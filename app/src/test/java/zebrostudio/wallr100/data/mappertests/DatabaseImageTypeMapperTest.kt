package zebrostudio.wallr100.data.mappertests

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.database.DatabaseImageType.*
import zebrostudio.wallr100.data.mapper.DatabaseImageTypeMapper
import zebrostudio.wallr100.data.mapper.DatabaseImageTypeMapperImpl
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType

@RunWith(MockitoJUnitRunner::class)
class DatabaseImageTypeMapperTest {

  private lateinit var databaseImageTypeMapper: DatabaseImageTypeMapper

  @Before
  fun setup() {
    databaseImageTypeMapper = DatabaseImageTypeMapperImpl()
  }

  @Test
  fun `should return Wallpaper type on mapToDatabaseImageType call success with input of CollectionsImageModel's Wallpaper type`() {
    assertEquals(WALLPAPER,
      databaseImageTypeMapper.mapToDatabaseImageType(
        CollectionsImageType.WALLPAPER))
  }

  @Test
  fun `should return Search type on mapToDatabaseImageType call success with input of CollectionsImageModel's Search type`() {
    assertEquals(SEARCH,
      databaseImageTypeMapper.mapToDatabaseImageType(
        CollectionsImageType.SEARCH))
  }

  @Test
  fun `should return Crystallized type on mapToDatabaseImageType call success with input of CollectionsImageModel's Crystallized type`() {
    assertEquals(CRYSTALLIZED,
      databaseImageTypeMapper.mapToDatabaseImageType(
        CollectionsImageType.CRYSTALLIZED))
  }

  @Test
  fun `should return Edited type on mapToDatabaseImageType call success with input of CollectionsImageModel's Edited type`() {
    assertEquals(EDITED,
      databaseImageTypeMapper.mapToDatabaseImageType(
        CollectionsImageType.EDITED))
  }

  @Test
  fun `should return Minimal color type on mapToDatabaseImageType call success with input of CollectionsImageModel's Minimal color type`() {
    assertEquals(MINIMAL_COLOR,
      databaseImageTypeMapper.mapToDatabaseImageType(
        CollectionsImageType.MINIMAL_COLOR))
  }
}