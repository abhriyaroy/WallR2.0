package zebrostudio.wallr100.imagelistfragment

import android.app.Application
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.reactivex.Single
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
import zebrostudio.wallr100.android.utils.FragmentTag.TOP_PICKS_TAG
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.dummylists.MockFirebaseImageList
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

private const val SPINNER_IDENTIFIER = "spinner"
private const val RECYCLERVIEW_IDENTIFIER = "recyclerview"

@RunWith(AndroidJUnit4::class)
class PopularFragmentTest : ImageListTestsBase() {

  private val testComponentRule =
      MockedRepositoryTestRule(InstrumentationRegistry.getTargetContext().applicationContext as Application)
  private val activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)
  @get: Rule
  var ruleChain: TestRule = RuleChain.outerRule(testComponentRule).around(activityTestRule)

  private lateinit var mockWallrRepository: WallrRepository

  @Before
  fun setup() {
    mockWallrRepository = testComponentRule.getTestAppComponent().wallrRepository
    `when`(mockWallrRepository.wasAppOpenedBefore()).thenReturn(true)
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getRecentPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getStandoutPictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_popular_images() {
    val imageList = MockFirebaseImageList.getList()
    `when`(mockWallrRepository.getPopularPictures()).thenReturn(
      getImageModelListAfterDelay(SECONDS.toMillis(1), MILLISECONDS, imageList))

    openPopularFragment()
    onView(allOf(withTagValue(`is`("${TOP_PICKS_TAG}_1_$SPINNER_IDENTIFIER")),
      withId(R.id.spinkitView))).check(matches(isCompletelyDisplayed()))
    verifyOnlyRecyclerViewIsVisibleAfterDelay(SECONDS.toMillis(1), "${TOP_PICKS_TAG}_1")
    verifyImagesDisplayed(imageList)
  }

  @Test
  fun should_display_unable_to_load_image_layout() {
    `when`(mockWallrRepository.getPopularPictures()).thenReturn(
      getErrorAfterDelayOnImageModelListCall(SECONDS.toMillis(1))
          .delay(SECONDS.toMillis(1), MILLISECONDS)
          .doOnError {
            Thread.sleep(SECONDS.toMillis(1))
          })

    openPopularFragment()
    onView(allOf(withTagValue(`is`("${TOP_PICKS_TAG}_1_$SPINNER_IDENTIFIER")),
      withId(R.id.spinkitView))).check(matches(isDisplayed()))
    verifyOnlyErrorLayoutIsVisibleAfterDelay(SECONDS.toMillis(1), "${TOP_PICKS_TAG}_1")
  }

  private fun openPopularFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.top_picks_title)).perform(click())
    onView(withText("POPULAR")).perform(click())
  }

  private fun verifyImagesDisplayed(list: List<FirebaseImageEntity>) {
    for (position in 0 until list.size) {
      onView(allOf(withTagValue(`is`("${TOP_PICKS_TAG}_1_$RECYCLERVIEW_IDENTIFIER")),
        withId(R.id.recyclerView)))
          .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(position))
          .check(matches(hasImageViewWithTagAtPosition(position,
            R.id.imageView,
            list[position].imageLinks.thumb)))
    }
  }
}