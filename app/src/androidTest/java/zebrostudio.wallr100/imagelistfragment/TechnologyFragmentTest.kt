package zebrostudio.wallr100.imagelistfragment

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
import zebrostudio.wallr100.imagelistfragment.BaseImageListFragmentTest.ImageListType.TECHNOLOGY

class TechnologyFragmentTest : BaseImageListFragmentTest() {
  @Before
  fun setup() {
    tagPrefix = "${FragmentTag.CATEGORIES_TAG}_5"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getBuildingsPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getFoodPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getObjectsPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getNaturePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getPeoplePictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_technology_images_on_getTechnologyPictures_call_success() {
    verifyImages(TECHNOLOGY) {
      openTechnologyFragment()
    }
  }

  @Test
  fun should_display_unable_to_load_image_layout_on_getTechnologyPictures_call_failure() {
    verifyUnableToLoadImagesLayout(TECHNOLOGY) {
      openTechnologyFragment()
    }
  }

  private fun openTechnologyFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.categories_title)).perform(click())
    onView(withText("FOOD")).perform(click())
    onView(withText("NATURE")).perform(click())
    onView(withText("OBJECTS")).perform(click())
    onView(withText("PEOPLE")).perform(click())
    onView(withText("TECHNOLOGY")).perform(click())
  }
}