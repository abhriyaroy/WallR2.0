package zebrostudio.wallr100.tests.imagelistfragments

import android.app.Application
import android.os.SystemClock
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import io.reactivex.Single
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.tests.BaseAndroidTest
import zebrostudio.wallr100.MockedRepositoryTestRule
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.adapters.ViewHolder
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapperImpl
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.dummylists.MockFirebaseImageList
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS

internal const val SPINNER_IDENTIFIER = "spinner"
internal const val RECYCLERVIEW_IDENTIFIER = "recyclerview"
internal const val ERROR_INFO_LAYOUT_IDENTIFIER = "error_info"

abstract class BaseImageListFragmentTest : BaseAndroidTest() {

  private val testComponentRule =
      MockedRepositoryTestRule(InstrumentationRegistry.getTargetContext().applicationContext as Application)
  protected val activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)
  @get: Rule
  var ruleChain: TestRule =
      RuleChain.outerRule(testComponentRule).around(activityTestRule)

  protected lateinit var tagPrefix: String
  protected lateinit var mockWallrRepository: WallrRepository

  fun initMocks() {
    mockWallrRepository = testComponentRule.getTestAppComponent().wallrRepository
    `when`(mockWallrRepository.wasAppOpenedBefore()).thenReturn(true)
  }

  protected fun verifyImages(imageListType: ImageListType, openImageListFragment: () -> Unit) {
    val imageList = MockFirebaseImageList.getList()
    `when`(getImageList(imageListType))
        .thenReturn(getImageModelListAfterDelay(SECONDS.toMillis(1),
          MILLISECONDS, imageList))
    openImageListFragment()
    onView(allOf(withTagValue(`is`("${tagPrefix}_$SPINNER_IDENTIFIER")),
      withId(R.id.spinkitView))).check(ViewAssertions.matches(isDisplayed()))
    verifyOnlyRecyclerViewIsVisibleAfterDelay(SECONDS.toMillis(1))
    matchImagesDisplayed(imageList)
  }

  protected fun verifyUnableToLoadImagesLayout(imageListType: ImageListType,
    openImageListFragment: () -> Unit) {
    `when`(getImageList(imageListType)).thenReturn(getErrorAfterDelayOnImageModelListCall(
      SECONDS.toMillis(1))
        .delay(SECONDS.toMillis(1), MILLISECONDS)
        .doOnError {
          Thread.sleep(SECONDS.toMillis(1))
        })

    openImageListFragment()
    onView(allOf(withTagValue(`is`("${tagPrefix}_$SPINNER_IDENTIFIER")),
      withId(R.id.spinkitView))).check(ViewAssertions.matches(isDisplayed()))
    verifyOnlyErrorLayoutIsVisibleAfterDelay(SECONDS.toMillis(1))
  }

  private fun matchImagesDisplayed(list: List<FirebaseImageEntity>) {
    for (position in 0 until list.size) {
      onView(allOf(withTagValue(`is`("${tagPrefix}_$RECYCLERVIEW_IDENTIFIER")),
        withId(R.id.recyclerView)))
          .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(position))
          .check(ViewAssertions.matches(hasImageViewWithTagAtPosition(position,
            R.id.imageView,
            list[position].imageLinks.thumb)))
    }
  }

  private fun getImageModelListAfterDelay(delay: Long, timeUnit: TimeUnit,
    imageList: List<FirebaseImageEntity>): Single<List<ImageModel>> {
    return Single.just(FirebasePictureEntityMapperImpl().mapFromEntity(imageList))
        .delay(delay, timeUnit)
  }

  private fun getErrorAfterDelayOnImageModelListCall(delay: Long): Single<List<ImageModel>> {
    return Single.create {
      SystemClock.sleep(delay)
      it.onError(Exception())
    }
  }

  private fun verifyOnlyRecyclerViewIsVisibleAfterDelay(delay: Long) {
    Thread.sleep(delay)
    onView(withTagValue(`is`("${tagPrefix}_$RECYCLERVIEW_IDENTIFIER")))
        .check(ViewAssertions.matches(isDisplayed()))
    onView(withTagValue(`is`("${tagPrefix}_$SPINNER_IDENTIFIER")))
        .check(ViewAssertions.matches(not(isDisplayed())))
    onView(withTagValue(`is`("${tagPrefix}_$ERROR_INFO_LAYOUT_IDENTIFIER")))
        .check(ViewAssertions.matches(not(isDisplayed())))
  }

  private fun verifyOnlyErrorLayoutIsVisibleAfterDelay(delay: Long) {
    Thread.sleep(delay)
    onView(withTagValue(`is`("${tagPrefix}_$ERROR_INFO_LAYOUT_IDENTIFIER")))
        .check(ViewAssertions.matches(isCompletelyDisplayed()))
    onView(allOf(withId(R.id.infoImageView),
      isDescendantOfA(withTagValue(`is`("${tagPrefix}_$ERROR_INFO_LAYOUT_IDENTIFIER")))))
        .check(ViewAssertions.matches(isCompletelyDisplayed()))
    onView(allOf(withId(R.id.infoTextFirstLine),
      isDescendantOfA(withTagValue(`is`("${tagPrefix}_$ERROR_INFO_LAYOUT_IDENTIFIER")))))
        .check(ViewAssertions.matches(isCompletelyDisplayed()))
    onView(allOf(withId(R.id.infoTextSecondLine),
      isDescendantOfA(withTagValue(`is`("${tagPrefix}_$ERROR_INFO_LAYOUT_IDENTIFIER")))))
        .check(ViewAssertions.matches(isCompletelyDisplayed()))
    onView(allOf(withId(R.id.infoTextThirdLine),
      isDescendantOfA(withTagValue(`is`("${tagPrefix}_$ERROR_INFO_LAYOUT_IDENTIFIER")))))
        .check(ViewAssertions.matches(isCompletelyDisplayed()))
    onView(withTagValue(`is`("${tagPrefix}_$SPINNER_IDENTIFIER")))
        .check(ViewAssertions.matches(not(isDisplayed())))
    onView(withTagValue(`is`("${tagPrefix}_$RECYCLERVIEW_IDENTIFIER")))
        .check(ViewAssertions.matches(not(isDisplayed())))
  }

  private fun getImageList(imageListType: ImageListType): Single<List<ImageModel>> {
    return when (imageListType.ordinal) {
      0 -> mockWallrRepository.getExplorePictures()
      1 -> mockWallrRepository.getRecentPictures()
      2 -> mockWallrRepository.getPopularPictures()
      3 -> mockWallrRepository.getStandoutPictures()
      4 -> mockWallrRepository.getBuildingsPictures()
      5 -> mockWallrRepository.getFoodPictures()
      6 -> mockWallrRepository.getNaturePictures()
      7 -> mockWallrRepository.getObjectsPictures()
      8 -> mockWallrRepository.getPeoplePictures()
      else -> mockWallrRepository.getTechnologyPictures()
    }
  }

  protected enum class ImageListType {
    EXPLORE,
    RECENT,
    POPULAR,
    STANDOUT,
    BUILDINGS,
    FOOD,
    NATURE,
    OBJECTS,
    PEOPLE,
    TECHNOLOGY
  }
}