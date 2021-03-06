package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcherImpl
import zebrostudio.wallr100.android.utils.FragmentTag.*
import zebrostudio.wallr100.android.utils.ResourceUtils

@RunWith(MockitoJUnitRunner::class)
class FragmentNameTagFetcherImplTest {

  @Mock
  lateinit var resourceUtils: ResourceUtils
  private lateinit var fragmentNameTag: FragmentNameTagFetcherImpl

  @Before
  fun setup() {
    fragmentNameTag = FragmentNameTagFetcherImpl(resourceUtils)
  }

  @Test
  fun `should return explore string on getFragmentName with explore tag call success`() {
    val exploreName = "EXPLORE"
    `when`(resourceUtils.getStringResource(R.string.explore_title)).thenReturn(exploreName)

    val fragmentName = fragmentNameTag.getFragmentName(EXPLORE_TAG)

    assertTrue(fragmentName == exploreName)
    verify(resourceUtils).getStringResource(R.string.explore_title)
  }

  @Test
  fun `should return top picks string on getFragmentName with top picks tag call success`() {
    val topPicksName = "TOP PICKS"
    `when`(resourceUtils.getStringResource(R.string.top_picks_title)).thenReturn(
      topPicksName)

    val fragmentName = fragmentNameTag.getFragmentName(TOP_PICKS_TAG)

    assertTrue(fragmentName == topPicksName)
    verify(resourceUtils).getStringResource(R.string.top_picks_title)
  }

  @Test
  fun `should return categories string on getFragmentName with categories tag call success`() {
    val categoriesName = "CATEGORIES"
    `when`(resourceUtils.getStringResource(R.string.categories_title)).thenReturn(
      categoriesName)

    val fragmentName = fragmentNameTag.getFragmentName(CATEGORIES_TAG)

    assertTrue(fragmentName == categoriesName)
    verify(resourceUtils).getStringResource(R.string.categories_title)
  }

  @Test
  fun `should return minimal string on getFragmentName with minimal tag call success`() {
    val minimalName = "MINIMAL"
    `when`(resourceUtils.getStringResource(R.string.minimal_title)).thenReturn(minimalName)

    val fragmentName = fragmentNameTag.getFragmentName(MINIMAL_TAG)

    assertTrue(fragmentName == minimalName)
    verify(resourceUtils).getStringResource(R.string.minimal_title)
  }

  @Test
  fun `should return collections string on getFragmentName with collections tag call success`() {
    val collectionsName = "COLLECTIONS"
    `when`(resourceUtils.getStringResource(R.string.collection_title)).thenReturn(
      collectionsName)

    val fragmentName = fragmentNameTag.getFragmentName(COLLECTIONS_TAG)

    assertTrue(fragmentName == collectionsName)
    verify(resourceUtils).getStringResource(R.string.collection_title)
  }

  @After
  fun tearDown() {
    verifyNoMoreInteractions(resourceUtils)
  }

}