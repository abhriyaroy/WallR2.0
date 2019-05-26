package zebrostudio.wallr100.presentation.mappertests

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageModel
import zebrostudio.wallr100.presentation.collection.Model.CollectionsPresenterEntity
import zebrostudio.wallr100.presentation.collection.mapper.CollectionImagesPresenterEntityMapper
import zebrostudio.wallr100.presentation.collection.mapper.CollectionsImagesPresenterEntityMapperImpl
import java.util.Random
import java.util.UUID

@RunWith(MockitoJUnitRunner::class)
class CollectionImagesPresenterEntityMapperTest {

  private lateinit var collectionsImagesPresenterEntityMapper: CollectionImagesPresenterEntityMapper
  private lateinit var collectionImagesModelList: List<CollectionsImageModel>
  private lateinit var collectionPresenterEntity: CollectionsPresenterEntity
  private lateinit var collectionPresenterEntityList: List<CollectionsPresenterEntity>

  @Before fun setup() {
    collectionsImagesPresenterEntityMapper = CollectionsImagesPresenterEntityMapperImpl()
    val uid = Random().nextLong()
    val name = UUID.randomUUID().toString()
    val path = UUID.randomUUID().toString()
    val data = UUID.randomUUID().toString()
    val type = Random().nextInt()
    collectionImagesModelList = listOf(CollectionsImageModel(uid, name, path, data, type))
    collectionPresenterEntity = CollectionsPresenterEntity(uid, name, path, data, type)
    collectionPresenterEntityList = listOf(collectionPresenterEntity)
  }

  @Test
  fun `should return list of CollectionsPresenterEntity on mapToPresenterEntity call success`() {
    val result =
        collectionsImagesPresenterEntityMapper.mapToPresenterEntity(collectionImagesModelList)

    assertEquals(collectionPresenterEntityList, result)
  }

  @Test
  fun `should return list of CollectionsImageModel on mapFromPresenterEntity call success with presenter entity list as argument`() {
    val result =
        collectionsImagesPresenterEntityMapper.mapFromPresenterEntity(collectionPresenterEntityList)

    assertEquals(collectionImagesModelList, result)
  }

  @Test
  fun `should return list of CollectionsImageModel on mapToPresenterEntity call success with presenter entity as argument`() {
    val result =
        collectionsImagesPresenterEntityMapper.mapFromPresenterEntity(collectionPresenterEntity)

    assertEquals(collectionImagesModelList, result)
  }
}