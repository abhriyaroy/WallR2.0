package zebrostudio.wallr100.imagelistfragment

import android.support.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import zebrostudio.wallr100.android.utils.FragmentTag.EXPLORE_TAG
import zebrostudio.wallr100.imagelistfragment.BaseImageListFragmentTest.ImageListType.EXPLORE

@RunWith(AndroidJUnit4::class)
class ExploreFragmentTest : BaseImageListFragmentTest() {

  @Before
  fun setup() {
    initMocks()
    tagPrefix = "${EXPLORE_TAG}_0"
  }

  @Test
  fun should_display_explore_images_on_getExplorePictures_call_success() {
    verifyImages(EXPLORE) {
      openExploreFragment()
    }
  }

  @Test
  fun should_display_unable_to_load_image_layout_on_getExplorePictures_call_success() {
    verifyUnableToLoadImagesLayout(EXPLORE) {
      openExploreFragment()
    }
  }

  private fun openExploreFragment() {
    activityTestRule.launchActivity(null)
  }
}