package zebrostudio.wallr100.data

import android.content.Context
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.data.database.DatabaseHelper
import zebrostudio.wallr100.rules.TrampolineSchedulerRule

@RunWith(MockitoJUnitRunner::class)
class ImageHandlerTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()
  @Mock lateinit var fileHandler: FileHandler
  @Mock lateinit var context: Context
  @Mock lateinit var databaseHelper: DatabaseHelper
  @Mock lateinit var wallpaperSetter: WallpaperSetter
  private lateinit var imageHandlerImpl: ImageHandlerImpl

  @Before
  fun setup() {
    imageHandlerImpl = ImageHandlerImpl(context, fileHandler, databaseHelper, wallpaperSetter)
  }

  @Test fun `should set continueFetchingImage to false on cancelFetchingImage call success`() {
    imageHandlerImpl.cancelFetchingImage()

    assertEquals(imageHandlerImpl.shouldContinueFetchingImage, false)
  }

  @Test fun `should complete on clearImageCache call success`() {
    imageHandlerImpl.clearImageCache().test().assertComplete()

    verify(fileHandler).deleteCacheFiles()
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(fileHandler, context, databaseHelper, wallpaperSetter)
  }
}