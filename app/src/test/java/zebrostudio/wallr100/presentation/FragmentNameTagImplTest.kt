package zebrostudio.wallr100.presentation

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentNameTagImpl
import zebrostudio.wallr100.android.utils.ResourceUtils

@RunWith(MockitoJUnitRunner::class)
class FragmentNameTagImplTest {

  @Mock lateinit var resourceUtils: ResourceUtils
  private lateinit var fragmentNameTag: FragmentNameTagImpl

  @Before fun setup() {
    fragmentNameTag = FragmentNameTagImpl(resourceUtils)
  }

  @Test fun `should return string on getTag call success`() {
    `when`(resourceUtils.getStringResource(R.string.explore_fragment_tag)).thenReturn("EXPLORE")

    val fragmentTag = fragmentNameTag.getTag(R.string.explore_fragment_tag)

    verify(resourceUtils).getStringResource(R.string.explore_fragment_tag)
    verifyNoMoreInteractions(resourceUtils)
    assertTrue(fragmentTag == "EXPLORE")
  }
}