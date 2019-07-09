package zebrostudio.wallr100.tests.imagelistfragments.categories

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentTag
import zebrostudio.wallr100.tests.imagelistfragments.BaseImageListFragmentTest
import zebrostudio.wallr100.tests.imagelistfragments.BaseImageListFragmentTest.ImageListType.NATURE

class NatureFragmentTest : BaseImageListFragmentTest() {
  @Before
  fun setup() {
    tagPrefix = "${FragmentTag.CATEGORIES_TAG}_2"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getBuildingsPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getFoodPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getObjectsPictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_nature_images_on_getNaturePictures_call_success() {
    verifyImages(NATURE) {
      openNatureFragment()
    }
  }

  @Test
  fun should_display_unable_to_load_image_layout_on_getNaturePictures_call_failure() {
    verifyUnableToLoadImagesLayout(NATURE) {
      openNatureFragment()
    }
  }

  private fun openNatureFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.categories_title)).perform(click())
    onView(withText("FOOD")).perform(click())
    onView(withText("NATURE")).perform(click())
  }
}