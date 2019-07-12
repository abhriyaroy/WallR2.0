package zebrostudio.wallr100.tests.imagelistfragments

import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.runner.AndroidJUnit4
import io.reactivex.Single
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mockito
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentTag.COLLECTIONS_TAG

@RunWith(AndroidJUnit4::class)
class CollectionsFragmentTest : BaseImageListFragmentTest() {

  @Before
  fun setup() {
    tagPrefix = "$COLLECTIONS_TAG"
    initMocks()
    Mockito.`when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
  }

  private fun openCollectionsFragment() {
    activityTestRule.launchActivity(null)
    Espresso.onView(ViewMatchers.withId(R.id.contentHamburger)).perform(click())
    Espresso.onView(ViewMatchers.withId(R.string.collection_title)).perform(click())
  }

}