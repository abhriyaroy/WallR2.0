package zebrostudio.wallr100.imagelistfragment

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.matcher.ViewMatchers
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentTag.CATEGORIES_TAG
import zebrostudio.wallr100.imagelistfragment.BaseImageListFragmentTest.ImageListType.BUILDINGS

class BuildingsFragmentTest : BaseImageListFragmentTest() {
  @Before
  fun setup() {
    tagPrefix = "${CATEGORIES_TAG}_0"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getFoodPictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_buildings_images_on_getBuildingsPictures_call_success() {
    verifyImages(BUILDINGS) {
      openBuildingsFragment()
    }
  }

  @Test
  fun should_display_unable_to_load_image_layout_on_getBuildingsPictures_call_failure() {
    verifyUnableToLoadImagesLayout(BUILDINGS) {
      openBuildingsFragment()
    }
  }

  private fun openBuildingsFragment() {
    activityTestRule.launchActivity(null)
    onView(ViewMatchers.withId(R.id.contentHamburger)).perform(ViewActions.click())
    onView(ViewMatchers.withId(R.string.categories_title)).perform(ViewActions.click())
  }
}