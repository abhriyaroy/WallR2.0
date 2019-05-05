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
import zebrostudio.wallr100.data.mapper.DatabaseImageTypeMapper
import zebrostudio.wallr100.data.mapper.DatabaseImageTypeMapperImpl
import zebrostudio.wallr100.domain.model.CollectionsImageModel

@RunWith(MockitoJUnitRunner::class)
class DatabaseImageTypeMapperTest {

  private lateinit var databaseImageTypeMapper: DatabaseImageTypeMapper

  @Before fun setup() {
    databaseImageTypeMapper = DatabaseImageTypeMapperImpl()
  }

  @Test
  fun `should return Wallpaper type on mapToDatabaseImageType call success with input of CollectionsImageModel's Wallpaper type`() {
    assertEquals(WALLPAPER,
        databaseImageTypeMapper.mapToDatabaseImageType(CollectionsImageModel.WALLPAPER))
  }

  @Test
  fun `should return Search type on mapToDatabaseImageType call success with input of CollectionsImageModel's Search type`() {
    assertEquals(SEARCH,
        databaseImageTypeMapper.mapToDatabaseImageType(CollectionsImageModel.SEARCH))
  }

  @Test
  fun `should return Crystallized type on mapToDatabaseImageType call success with input of CollectionsImageModel's Crystallized type`() {
    assertEquals(CRYSTALLIZED,
        databaseImageTypeMapper.mapToDatabaseImageType(CollectionsImageModel.CRYSTALLIZED))
  }

  @Test
  fun `should return Edited type on mapToDatabaseImageType call success with input of CollectionsImageModel's Edited type`() {
    assertEquals(EDITED,
        databaseImageTypeMapper.mapToDatabaseImageType(CollectionsImageModel.EDITED))
  }

  @Test
  fun `should return Minimal color type on mapToDatabaseImageType call success with input of CollectionsImageModel's Minimal color type`() {
    assertEquals(MINIMAL_COLOR,
        databaseImageTypeMapper.mapToDatabaseImageType(CollectionsImageModel.MINIMAL_COLOR))
  }
}