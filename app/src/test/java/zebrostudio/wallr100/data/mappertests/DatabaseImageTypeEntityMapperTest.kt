package zebrostudio.wallr100.data.mappertests

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.database.DatabaseImageType.CRYSTALLIZED
import zebrostudio.wallr100.data.database.DatabaseImageType.EDITED
import zebrostudio.wallr100.data.database.DatabaseImageType.MINIMAL_COLOR
import zebrostudio.wallr100.data.database.DatabaseImageType.SEARCH
import zebrostudio.wallr100.data.database.DatabaseImageType.WALLPAPER
import zebrostudio.wallr100.data.mapper.DatabaseImageTypeEntityMapper
import zebrostudio.wallr100.data.mapper.DatabaseImageTypeEntityMapperImpl
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageTypeModel

@RunWith(MockitoJUnitRunner::class)
class DatabaseImageTypeEntityMapperTest {

  private lateinit var databaseImageTypeEntityMapper: DatabaseImageTypeEntityMapper

  @Before fun setup() {
    databaseImageTypeEntityMapper = DatabaseImageTypeEntityMapperImpl()
  }

  @Test
  fun `should return Wallpaper type on mapToDatabaseImageType call success with input of CollectionsImageModel's Wallpaper type`() {
    assertEquals(WALLPAPER,
        databaseImageTypeEntityMapper.mapToDatabaseImageType(
            CollectionsImageTypeModel.WALLPAPER))
  }

  @Test
  fun `should return Search type on mapToDatabaseImageType call success with input of CollectionsImageModel's Search type`() {
    assertEquals(SEARCH,
        databaseImageTypeEntityMapper.mapToDatabaseImageType(
            CollectionsImageTypeModel.SEARCH))
  }

  @Test
  fun `should return Crystallized type on mapToDatabaseImageType call success with input of CollectionsImageModel's Crystallized type`() {
    assertEquals(CRYSTALLIZED,
        databaseImageTypeEntityMapper.mapToDatabaseImageType(
            CollectionsImageTypeModel.CRYSTALLIZED))
  }

  @Test
  fun `should return Edited type on mapToDatabaseImageType call success with input of CollectionsImageModel's Edited type`() {
    assertEquals(EDITED,
        databaseImageTypeEntityMapper.mapToDatabaseImageType(
            CollectionsImageTypeModel.EDITED))
  }

  @Test
  fun `should return Minimal color type on mapToDatabaseImageType call success with input of CollectionsImageModel's Minimal color type`() {
    assertEquals(MINIMAL_COLOR,
        databaseImageTypeEntityMapper.mapToDatabaseImageType(
            CollectionsImageTypeModel.MINIMAL_COLOR))
  }
}