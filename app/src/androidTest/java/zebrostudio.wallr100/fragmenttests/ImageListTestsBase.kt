package zebrostudio.wallr100.fragmenttests

import android.os.SystemClock
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers.*
import io.reactivex.Single
import org.hamcrest.CoreMatchers.not
import zebrostudio.wallr100.AndroidTestBase
import zebrostudio.wallr100.R
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapperImpl
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.domain.model.images.ImageModel
import java.util.concurrent.TimeUnit

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

  protected fun verifyOnlyRecyclerViewIsVisibleAfterDelay(delay: Long) {
    Thread.sleep(delay)
    Espresso.onView(withId(R.id.recyclerView))
        .check(ViewAssertions.matches(isDisplayed()))
    Espresso.onView(withId(R.id.spinkitView))
        .check(ViewAssertions.matches(not(isDisplayed())))
    Espresso.onView(withId(R.id.infoImageView))
        .check(ViewAssertions.matches(not(isDisplayed())))
    Espresso.onView(withId(R.id.infoTextFirstLine))
        .check(ViewAssertions.matches(not(isDisplayed())))
    Espresso.onView(withId(R.id.infoTextSecondLine))
        .check(ViewAssertions.matches(not(isDisplayed())))
    Espresso.onView(withId(R.id.infoTextThirdLine))
        .check(ViewAssertions.matches(not(isDisplayed())))
  }

  protected fun verifyOnlyErrorLayoutIsVisibleAfterDelay(delay: Long) {
    Thread.sleep(delay)
    Espresso.onView(withId(R.id.errorInfoRelativeLayout))
        .check(ViewAssertions.matches(isCompletelyDisplayed()))
    Espresso.onView(withId(R.id.infoImageView))
        .check(ViewAssertions.matches(isCompletelyDisplayed()))
    Espresso.onView(withId(R.id.infoTextFirstLine))
        .check(ViewAssertions.matches(isCompletelyDisplayed()))
    Espresso.onView(withId(R.id.infoTextSecondLine))
        .check(ViewAssertions.matches(isCompletelyDisplayed()))
    Espresso.onView(withId(R.id.infoTextThirdLine))
        .check(ViewAssertions.matches(isCompletelyDisplayed()))
    Espresso.onView(withId(R.id.spinkitView))
        .check(ViewAssertions.matches(not(isDisplayed())))
    Espresso.onView(withId(R.id.recyclerView))
        .check(ViewAssertions.matches(not(isDisplayed())))
  }
}