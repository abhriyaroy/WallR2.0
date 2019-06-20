package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerContract.CollectionsRecyclerItemViewHolder
import zebrostudio.wallr100.presentation.adapters.CollectionRecyclerPresenterImpl
import zebrostudio.wallr100.presentation.datafactory.CollectionImagesPresenterEntityFactory.getCollectionImagesPresenterEntity
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class CollectionRecyclerPresenterImplTest {

  @Mock
  private lateinit var collectionsRecyclerItemViewHolder: CollectionsRecyclerItemViewHolder
  private lateinit var collectionRecyclerPresenterImpl: CollectionRecyclerPresenterImpl
  private val randomString = randomUUID().toString()

  @Before
  fun setup(){
    collectionRecyclerPresenterImpl = CollectionRecyclerPresenterImpl()
  }

  @Test
  fun `should return size of image list on getItemCount call success`() {
    assertEquals(1,
      collectionRecyclerPresenterImpl.getItemCount(listOf(getCollectionImagesPresenterEntity())))
  }

  @Test
  fun `should show image without selected indicator on onBindRepositoryRowViewAtPosition call success`() {
    val item0 = getCollectionImagesPresenterEntity()
    val item1 = getCollectionImagesPresenterEntity()
    val item2 = getCollectionImagesPresenterEntity()
    val imagePathList = listOf(item0, item1, item2)
    val selectedIndices = hashMapOf(Pair(0, item0), Pair(2, item2))
    val position = 1

    collectionRecyclerPresenterImpl.onBindRepositoryRowViewAtPosition(
      collectionsRecyclerItemViewHolder,
      imagePathList,
      selectedIndices,
      position)

    verify(collectionsRecyclerItemViewHolder).setImage(item1.path)
    verify(collectionsRecyclerItemViewHolder).attachClickListener()
    verify(collectionsRecyclerItemViewHolder).attachLongClickToDragListener()
    verify(collectionsRecyclerItemViewHolder).hideSelectedIndicator()
  }

  @Test
  fun `should show image with selected indicator on onBindRepositoryRowViewAtPosition call success`() {
    val item0 = getCollectionImagesPresenterEntity()
    val item1 = getCollectionImagesPresenterEntity()
    val item2 = getCollectionImagesPresenterEntity()
    val imagePathList = listOf(item0, item1, item2)
    val selectedIndices = hashMapOf(Pair(0, item0), Pair(2, item2))
    val position = 2

    collectionRecyclerPresenterImpl.onBindRepositoryRowViewAtPosition(
      collectionsRecyclerItemViewHolder,
      imagePathList,
      selectedIndices,
      position)

    verify(collectionsRecyclerItemViewHolder).setImage(item2.path)
    verify(collectionsRecyclerItemViewHolder).attachClickListener()
    verify(collectionsRecyclerItemViewHolder).attachLongClickToDragListener()
    verify(collectionsRecyclerItemViewHolder).showSelectedIndicator()
  }

  @After
  fun tearDown(){
    verifyNoMoreInteractions(collectionsRecyclerItemViewHolder)
  }
}