package zebrostudio.wallr100.imagelistfragment

import android.os.SystemClock
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers.*
import io.reactivex.Single
import org.hamcrest.CoreMatchers.*
import zebrostudio.wallr100.AndroidTestBase
import zebrostudio.wallr100.R
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapperImpl
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.domain.model.images.ImageModel
import java.util.concurrent.TimeUnit

private const val SPINNER_IDENTIFIER = "spinner"
private const val RECYCLERVIEW_IDENTIFIER = "recyclerview"
private const val ERROR_INFO_LAYOUT_IDENTIFIER = "error_info"

abstract class ImageListTestsBase : AndroidTestBase() {

  protected fun getImageModelListAfterDelay(delay: Long, timeUnit: TimeUnit,
    imageList: List<FirebaseImageEntity>): Single<List<ImageModel>> {
    return Single.just(FirebasePictureEntityMapperImpl().mapFromEntity(imageList))
        .delay(delay, timeUnit)
  }

  protected fun getErrorAfterDelayOnImageModelListCall(delay: Long): Single<List<ImageModel>> {
    return Single.create {
      SystemClock.sleep(delay)
      it.onError(Exception())
    }
  }

  protected fun verifyOnlyRecyclerViewIsVisibleAfterDelay(delay: Long, tagPrefix: String) {
    Thread.sleep(delay)
    onView(withTagValue(`is`("${tagPrefix}_$RECYCLERVIEW_IDENTIFIER")))
        .check(ViewAssertions.matches(isDisplayed()))
    onView(withTagValue(`is`("${tagPrefix}_$SPINNER_IDENTIFIER")))
        .check(ViewAssertions.matches(not(isDisplayed())))
    onView(withTagValue(`is`("${tagPrefix}_$ERROR_INFO_LAYOUT_IDENTIFIER")))
        .check(ViewAssertions.matches(not(isDisplayed())))
  }

  protected fun verifyOnlyErrorLayoutIsVisibleAfterDelay(delay: Long, tagPrefix: String) {
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
}