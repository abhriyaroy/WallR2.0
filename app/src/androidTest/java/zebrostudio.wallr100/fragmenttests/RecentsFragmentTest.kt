package zebrostudio.wallr100.fragmenttests

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
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
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
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.dummylists.MockFirebaseImageList
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class RecentsFragmentTest : ImageListTestsBase() {

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
    `when`(mockWallrRepository.getPopularPictures()).thenReturn(Single.error(Exception()))
    `when`(mockWallrRepository.getStandoutPictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_display_explore_images() {
    val imageList = MockFirebaseImageList.getList()
    `when`(mockWallrRepository.getRecentPictures()).thenReturn(
      getImageModelListAfterDelay(TimeUnit.SECONDS.toMillis(1), TimeUnit.MILLISECONDS, imageList))

    openRecentsFragment()
    onView(allOf(withId(R.id.spinkitView), withTagValue(`is`(1 as Any))))
        .check(matches(isDisplayed()))
    verifyOnlyRecyclerViewIsVisibleAfterDelay(TimeUnit.SECONDS.toMillis(1))
    verifyImagesDisplayed(imageList)
  }

  @Test
  fun should_display_unable_to_load_image_layout() {
    `when`(mockWallrRepository.getRecentPictures()).thenReturn(
      getErrorAfterDelayOnImageModelListCall(
        TimeUnit.SECONDS.toMillis(1))
          .delay(TimeUnit.SECONDS.toMillis(1), TimeUnit.MILLISECONDS)
          .doOnError {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1))
          })

    openRecentsFragment()
    onView(withId(R.id.spinkitView)).check(matches(isDisplayed()))
    verifyOnlyErrorLayoutIsVisibleAfterDelay(TimeUnit.SECONDS.toMillis(1))
  }

  private fun openRecentsFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.top_picks_title)).perform(click())
  }

  private fun verifyImagesDisplayed(list: List<FirebaseImageEntity>) {
    for (position in 0 until list.size) {
      onView(withId(R.id.recyclerView))
          .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(
            position))
          .check(matches(
            hasImageViewWithTagAtPosition(position,
              R.id.imageView,
              list[position].imageLinks.thumb)))
    }
  }

}