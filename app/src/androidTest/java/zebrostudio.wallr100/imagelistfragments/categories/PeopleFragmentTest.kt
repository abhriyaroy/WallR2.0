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
import zebrostudio.wallr100.imagelistfragments.BaseImageListFragmentTest.ImageListType.PEOPLE

class PeopleFragmentTest : BaseImageListFragmentTest() {
  @Before
  fun setup() {
    tagPrefix = "${FragmentTag.CATEGORIES_TAG}_4"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getBuildingsPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getFoodPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getNaturePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getObjectsPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getTechnologyPictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_people_images_on_getPeoplePictures_call_success() {
    verifyImages(PEOPLE) {
      openPeopleFragment()
    }
  }

  @Test
  fun should_display_unable_to_load_image_layout_on_getPeoplePictures_call_failure() {
    verifyUnableToLoadImagesLayout(PEOPLE) {
      openPeopleFragment()
    }
  }

  private fun openPeopleFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.categories_title)).perform(click())
    onView(withText("FOOD")).perform(click())
    onView(withText("NATURE")).perform(click())
    onView(withText("OBJECTS")).perform(click())
    onView(withText("PEOPLE")).perform(click())
  }
}