package zebrostudio.wallr100.imagelistfragments.categories

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
import zebrostudio.wallr100.imagelistfragments.BaseImageListFragmentTest
import zebrostudio.wallr100.imagelistfragments.BaseImageListFragmentTest.ImageListType.OBJECTS

class ObjectsFragmentTest : BaseImageListFragmentTest() {
  @Before
  fun setup() {
    tagPrefix = "${FragmentTag.CATEGORIES_TAG}_3"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getBuildingsPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getFoodPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getNaturePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getPeoplePictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_objects_images_on_getObjectPictures_call_success() {
    verifyImages(OBJECTS) {
      openObjectsFragment()
    }
  }

  @Test
  fun should_display_unable_to_load_image_layout_on_getObjectPictures_call_failure() {
    verifyUnableToLoadImagesLayout(OBJECTS) {
      openObjectsFragment()
    }
  }

  private fun openObjectsFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.categories_title)).perform(click())
    onView(withText("FOOD")).perform(click())
    onView(withText("NATURE")).perform(click())
    onView(withText("OBJECTS")).perform(click())
  }
}