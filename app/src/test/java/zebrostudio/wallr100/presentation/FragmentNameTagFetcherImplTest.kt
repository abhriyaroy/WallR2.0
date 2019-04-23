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
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.CATEGORIES_TAG
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.COLLECTIONS_TAG
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.EXPLORE_TAG
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.MINIMAL_TAG
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcher.Companion.TOP_PICKS_TAG
import zebrostudio.wallr100.android.utils.FragmentNameTagFetcherImpl
import zebrostudio.wallr100.android.utils.ResourceUtils

@RunWith(MockitoJUnitRunner::class)
class FragmentNameTagFetcherImplTest {

  @Mock lateinit var resourceUtils: ResourceUtils
  private lateinit var fragmentNameTag: FragmentNameTagFetcherImpl

  @Before fun setup() {
    fragmentNameTag = FragmentNameTagFetcherImpl(resourceUtils)
  }

  @Test fun `should return explore string on getFragmentName with explore tag call success`() {
    val exploreTag = "EXPLORE"
    `when`(resourceUtils.getStringResource(R.string.explore_fragment_tag)).thenReturn(exploreTag)

    val fragmentTag = fragmentNameTag.getFragmentName(EXPLORE_TAG)

    assertTrue(fragmentTag == exploreTag)
    verify(resourceUtils).getStringResource(R.string.explore_fragment_tag)
  }

  @Test fun `should return top picks string on getFragmentName with top picks tag call success`() {
    val topPicksTag = "TOP PICKS"
    `when`(resourceUtils.getStringResource(R.string.top_picks_fragment_tag)).thenReturn(topPicksTag)

    val fragmentTag = fragmentNameTag.getFragmentName(TOP_PICKS_TAG)

    assertTrue(fragmentTag == topPicksTag)
    verify(resourceUtils).getStringResource(R.string.top_picks_fragment_tag)
  }

  @Test
  fun `should return categories string on getFragmentName with categories tag call success`() {
    val categoriesTag = "CATEGORIES"
    `when`(resourceUtils.getStringResource(R.string.categories_fragment_tag)).thenReturn(
        categoriesTag)

    val fragmentTag = fragmentNameTag.getFragmentName(CATEGORIES_TAG)

    assertTrue(fragmentTag == categoriesTag)
    verify(resourceUtils).getStringResource(R.string.categories_fragment_tag)
  }

  @Test fun `should return minimal string on getFragmentName with minimal tag call success`() {
    val minimalTag = "MINIMAL"
    `when`(resourceUtils.getStringResource(R.string.minimal_fragment_tag)).thenReturn(minimalTag)

    val fragmentTag = fragmentNameTag.getFragmentName(MINIMAL_TAG)

    assertTrue(fragmentTag == minimalTag)
    verify(resourceUtils).getStringResource(R.string.minimal_fragment_tag)
  }

  @Test
  fun `should return collections string on getFragmentName with collections tag call success`() {
    val collectionsTag = "COLLECTIONS"
    `when`(resourceUtils.getStringResource(R.string.collection_fragment_tag)).thenReturn(
        collectionsTag)

    val fragmentTag = fragmentNameTag.getFragmentName(COLLECTIONS_TAG)

    assertTrue(fragmentTag == collectionsTag)
    verify(resourceUtils).getStringResource(R.string.collection_fragment_tag)
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(resourceUtils)
  }

}