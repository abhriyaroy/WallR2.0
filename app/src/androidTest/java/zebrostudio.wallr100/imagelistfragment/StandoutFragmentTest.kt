package zebrostudio.wallr100.imagelistfragment

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentTag.TOP_PICKS_TAG
import zebrostudio.wallr100.imagelistfragment.BaseImageListFragmentTest.ImageListType.STANDOUT

@RunWith(AndroidJUnit4::class)
class StandoutFragmentTest : BaseImageListFragmentTest() {

  @Before
  fun setup() {
    tagPrefix = "${TOP_PICKS_TAG}_2"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getRecentPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getPopularPictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_standout_images_on_getStandoutPictures_call_success() {
    verifyImages(STANDOUT) {
      openStandoutFragment()
    }
  }

  @Test
  fun should_display_unable_to_load_image_layout_on_getStandoutPictures_call_failure() {
    verifyUnableToLoadImagesLayout(STANDOUT) {
      openStandoutFragment()
    }
  }

  private fun openStandoutFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.top_picks_title)).perform(click())
    onView(withText("POPULAR")).perform(click())
    onView(withText("STANDOUTS")).perform(click())
  }
}