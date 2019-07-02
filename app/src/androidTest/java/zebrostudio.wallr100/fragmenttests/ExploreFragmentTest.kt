package zebrostudio.wallr100.fragmenttests

import android.app.Application
import android.support.test.InstrumentationRegistry.getTargetContext
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
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
import java.util.concurrent.TimeUnit.MILLISECONDS

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
    `when`(mockWallrRepository.isAppOpenedForTheFirstTime()).thenReturn(true)
  }

  @Test
  fun should_display_explore_images() {
    val imageList = MockFirebaseImageList.getList()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(
      getImageModelListAfterDelay(TimeUnit.SECONDS.toMillis(1), MILLISECONDS, imageList))

    activityTestRule.launchActivity(null)
    onView(withId(R.id.spinkitView)).check(matches(isDisplayed()))
    verifyOnlyRecyclerViewIsVisibleAfterDelay(TimeUnit.SECONDS.toMillis(1))
    verifyImagesDisplayed(imageList)
  }

  @Test
  fun should_display_unable_to_load_image_layout() {
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(
      getErrorAfterDelayOnImageModelListCall(
        TimeUnit.SECONDS.toMillis(1))
          .delay(TimeUnit.SECONDS.toMillis(1), MILLISECONDS)
          .doOnError {
            Thread.sleep(TimeUnit.SECONDS.toMillis(1))
          })

    activityTestRule.launchActivity(null)
    onView(withId(R.id.spinkitView)).check(matches(isDisplayed()))
    verifyOnlyErrorLayoutIsVisibleAfterDelay(TimeUnit.SECONDS.toMillis(1))
  }

  private fun verifyImagesDisplayed(list: List<FirebaseImageEntity>) {
    for (position in 0 until list.size) {
      onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.scrollToPosition<ViewHolder>(
        position))
          .check(matches(
            hasImageViewWithTagAtPosition(position,
              R.id.imageView,
              list[position].imageLinks.thumb)))
    }
  }

}