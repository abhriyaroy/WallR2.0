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
import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerContract.DragSelectItemViewHolder
import zebrostudio.wallr100.presentation.adapters.DragSelectRecyclerPresenterImpl
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class DragSelectRecyclerPresenterImplTest {

  @Mock
  private lateinit var dragSelectItemViewHolder: DragSelectItemViewHolder
  private lateinit var dragSelectRecyclerIPresenterImpl: DragSelectRecyclerPresenterImpl
  private val randomString = randomUUID().toString()

  @Before
  fun before() {
    dragSelectRecyclerIPresenterImpl = DragSelectRecyclerPresenterImpl()
  }

  @Test
  fun `should return size of list incremented by 1 on getItemCount call success`() {
    val list = listOf<String>()

    assertEquals(1, dragSelectRecyclerIPresenterImpl.getItemCount(list))
  }

  @Test
  fun `should show add color layout on onBindRepositoryRowViewAtPosition call success`() {
    val list = listOf<String>()
    val map = hashMapOf<Int, String>()
    val position = 0

    dragSelectRecyclerIPresenterImpl.onBindRepositoryRowViewAtPosition(dragSelectItemViewHolder,
      list, map, position)

    verify(dragSelectItemViewHolder).hideSelectedIndicator()
    verify(dragSelectItemViewHolder).showAddImageLayout()
    verify(dragSelectItemViewHolder).attachClickListener()
  }

  @Test
  fun `should show color layout and hide selected indicator on onBindRepositoryRowViewAtPosition call success`() {
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    val position = 1

    dragSelectRecyclerIPresenterImpl.onBindRepositoryRowViewAtPosition(dragSelectItemViewHolder,
      list, map, position)

    verify(dragSelectItemViewHolder).hideSelectedIndicator()
    verify(dragSelectItemViewHolder).hideAddImageLayout()
    verify(dragSelectItemViewHolder).setImageViewColor(randomString)
    verify(dragSelectItemViewHolder).attachLongClickListener()
    verify(dragSelectItemViewHolder).attachClickListener()
  }

  @Test
  fun `should show color layout and show selected indicator on onBindRepositoryRowViewAtPosition call success`() {
    val list = mutableListOf<String>()
    list.add(randomString)
    val map = hashMapOf<Int, String>()
    map[0] = randomString
    val position = 1

    dragSelectRecyclerIPresenterImpl.onBindRepositoryRowViewAtPosition(dragSelectItemViewHolder,
      list, map, position)

    verify(dragSelectItemViewHolder).hideSelectedIndicator()
    verify(dragSelectItemViewHolder).hideAddImageLayout()
    verify(dragSelectItemViewHolder).setImageViewColor(randomString)
    verify(dragSelectItemViewHolder).attachLongClickListener()
    verify(dragSelectItemViewHolder).attachClickListener()
    verify(dragSelectItemViewHolder).showSelectedIndicator()
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(dragSelectItemViewHolder)
  }

}