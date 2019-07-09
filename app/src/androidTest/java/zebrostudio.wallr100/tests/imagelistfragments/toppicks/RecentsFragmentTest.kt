package zebrostudio.wallr100.tests.imagelistfragments.toppicks

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.runner.AndroidJUnit4
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentTag.TOP_PICKS_TAG
import zebrostudio.wallr100.tests.imagelistfragments.BaseImageListFragmentTest
import zebrostudio.wallr100.tests.imagelistfragments.BaseImageListFragmentTest.ImageListType.RECENT

@RunWith(AndroidJUnit4::class)
class RecentsFragmentTest : BaseImageListFragmentTest() {

  @Before
  fun setup() {
    tagPrefix = "${TOP_PICKS_TAG}_0"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getPopularPictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_recent_images_on_getRecentPictures_call_success() {
    verifyImages(RECENT) {
      openRecentsFragment()
    }
  }

  @Test
  fun should_display_unable_to_load_image_layout_on_getRecentPictures_call_failure() {
    verifyUnableToLoadImagesLayout(RECENT) {
      openRecentsFragment()
    }
  }

  private fun openRecentsFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.top_picks_title)).perform(click())
  }
}