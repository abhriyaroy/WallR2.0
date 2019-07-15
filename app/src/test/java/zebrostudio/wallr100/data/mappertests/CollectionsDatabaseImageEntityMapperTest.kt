package zebrostudio.wallr100.data.mappertests

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.database.entity.CollectionDatabaseImageEntity
import zebrostudio.wallr100.data.mapper.CollectionsDatabaseImageEntityMapper
import zebrostudio.wallr100.data.mapper.CollectionsDatabaseImageEntityMapperImpl
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageModel
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class CollectionsDatabaseImageEntityMapperTest {

  private lateinit var mapper: CollectionsDatabaseImageEntityMapper
  private var collectionDatabaseImageEntityList = mutableListOf<CollectionDatabaseImageEntity>()
  private var collectionsImageModelList = mutableListOf<CollectionsImageModel>()

  @Before
  fun setup() {
    mapper = CollectionsDatabaseImageEntityMapperImpl()
    val uid = Random().nextLong()
    val name = UUID.randomUUID().toString()
    val path = UUID.randomUUID().toString()
    val data = UUID.randomUUID().toString()
    val type = Random().nextInt()
    collectionDatabaseImageEntityList.add(
      CollectionDatabaseImageEntity(uid, name, path, data, type))
    collectionsImageModelList.add(CollectionsImageModel(uid, name, path, data, type))
  }

  @Test
  fun `should return list of CollectionsImageModel on mapFromEntity call success`() {
    assertEquals(collectionsImageModelList, mapper.mapFromEntity(collectionDatabaseImageEntityList))
  }

  @Test
  fun `should return list of CollectionDatabaseImageEntity on mapToEntity call success`() {
    assertEquals(collectionDatabaseImageEntityList, mapper.mapToEntity(collectionsImageModelList))
  }
}