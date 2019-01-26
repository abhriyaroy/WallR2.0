package zebrostudio.wallr100.data

import android.content.Context
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.rules.TrampolineSchedulerRule

@RunWith(MockitoJUnitRunner::class)
class ImageHandlerTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()
  @Mock lateinit var fileHandler: FileHandler
  @Mock lateinit var context: Context
  private lateinit var imageHandlerImpl: ImageHandlerImpl

  @Before
  fun setup() {
    imageHandlerImpl = ImageHandlerImpl(context, fileHandler)
  }

  @Test fun `should set continueFetchingImage to false on cancelFetchingImage call success`() {
    imageHandlerImpl.cancelFetchingImage()

    assertEquals(imageHandlerImpl.shouldContinueFetchingImage, false)
  }

  @Test fun `should complete on clearImageCache call success`() {
    imageHandlerImpl.clearImageCache().test().assertComplete()

    verify(fileHandler).deleteCacheFiles()
    verifyNoMoreInteractions(fileHandler)
  }
}