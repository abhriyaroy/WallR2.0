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
import zebrostudio.wallr100.tests.imagelistfragments.BaseImageListFragmentTest.ImageListType.FOOD

class FoodFragmentTest : BaseImageListFragmentTest() {
  @Before
  fun setup() {
    tagPrefix = "${FragmentTag.CATEGORIES_TAG}_1"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getBuildingsPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getNaturePictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_food_images_on_getFoodPictures_call_success() {
    verifyImages(FOOD) {
      openFoodFragment()
    }
  }

  @Test
  fun should_display_unable_to_load_image_layout_on_getFoodPictures_call_failure() {
    verifyUnableToLoadImagesLayout(FOOD) {
      openFoodFragment()
    }
  }

  private fun openFoodFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.categories_title)).perform(click())
    onView(withText("FOOD")).perform(click())
  }
}