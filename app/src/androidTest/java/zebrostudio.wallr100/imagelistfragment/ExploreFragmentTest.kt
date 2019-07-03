package zebrostudio.wallr100.imagelistfragment

import android.app.Application
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.MockedRepositoryTestRule
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.adapters.ViewHolder
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.utils.FragmentTag.EXPLORE_TAG
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.dummylists.MockFirebaseImageList
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

private const val SPINNER_IDENTIFIER = "spinner"
private const val RECYCLERVIEW_IDENTIFIER = "recyclerview"

@RunWith(AndroidJUnit4::class)
class ExploreFragmentTest : ImageListTestsBase() {

  private val testComponentRule =
      MockedRepositoryTestRule(getTargetContext().applicationContext as Application)
  private val activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)
  @get: Rule
  var ruleChain: TestRule = RuleChain.outerRule(testComponentRule).around(activityTestRule)

  private lateinit var mockWallrRepository: WallrRepository

  @Before
  fun setup() {
    mockWallrRepository = testComponentRule.getTestAppComponent().wallrRepository
    `when`(mockWallrRepository.wasAppOpenedBefore()).thenReturn(true)
  }

  @Test
  fun should_display_recent_images() {
    val imageList = MockFirebaseImageList.getList()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(
      getImageModelListAfterDelay(SECONDS.toMillis(1), MILLISECONDS, imageList))

    activityTestRule.launchActivity(null)
    onView(withId(R.id.spinkitView)).check(matches(isDisplayed()))
    verifyOnlyRecyclerViewIsVisibleAfterDelay(SECONDS.toMillis(1), "$EXPLORE_TAG-0-")
    verifyImagesDisplayed(imageList)
  }

  @Test
  fun should_display_unable_to_load_image_layout() {
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(
      getErrorAfterDelayOnImageModelListCall(
        SECONDS.toMillis(1))
          .delay(SECONDS.toMillis(1), MILLISECONDS)
          .doOnError {
            sleep(SECONDS.toMillis(1))
          })

    activityTestRule.launchActivity(null)
    onView(withId(R.id.spinkitView)).check(matches(isDisplayed()))
    verifyOnlyErrorLayoutIsVisibleAfterDelay(SECONDS.toMillis(1), "$EXPLORE_TAG-0-")
  }

  private fun verifyImagesDisplayed(list: List<FirebaseImageEntity>) {
    for (position in 0 until list.size) {
      onView(allOf(withTagValue(`is`("$EXPLORE_TAG-0-$RECYCLERVIEW_IDENTIFIER")),
        withId(R.id.recyclerView)))
          .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(position))
          .check(matches(hasImageViewWithTagAtPosition(position,
            R.id.imageView,
            list[position].imageLinks.thumb)))
    }
  }

}