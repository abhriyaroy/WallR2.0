package zebrostudio.wallr100.tests.imagelistfragments.toppicks

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
import zebrostudio.wallr100.tests.imagelistfragments.BaseImageListFragmentTest
import zebrostudio.wallr100.tests.imagelistfragments.BaseImageListFragmentTest.ImageListType.POPULAR

@RunWith(AndroidJUnit4::class)
class PopularFragmentTest : BaseImageListFragmentTest() {

  @Before
  fun setup() {
    tagPrefix = "${TOP_PICKS_TAG}_1"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getRecentPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getStandoutPictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_poplar_images_on_getPopularPictures_call_success() {
    verifyImages(POPULAR) {
      openPopularFragment()
    }
  }

  @Test
  fun should_display_unable_to_load_image_layout_on_getPopularPictures_call_failure() {
    verifyUnableToLoadImagesLayout(POPULAR) {
      openPopularFragment()
    }
  }

  private fun openPopularFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.top_picks_title)).perform(click())
    onView(withText("POPULAR")).perform(click())
  }
}