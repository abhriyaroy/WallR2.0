package zebrostudio.wallr100

import android.os.SystemClock
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import io.reactivex.Single
import org.hamcrest.CoreMatchers
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapperImpl
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.domain.model.images.ImageModel
import java.util.concurrent.TimeUnit

open class ImageListTestsBase : AndroidTestBase() {

  /*protected fun getImageModelListAfterDelay(delay: Long, timeUnit: TimeUnit,
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

  protected fun verifyOnlyRecyclerViewIsVisibleAfterDelay(delay: Long){
    Thread.sleep(delay)
    Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.spinkitView))
        .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    Espresso.onView(ViewMatchers.withId(R.id.infoImageView))
        .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    Espresso.onView(ViewMatchers.withId(R.id.infoTextFirstLine))
        .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    Espresso.onView(ViewMatchers.withId(R.id.infoTextSecondLine))
        .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    Espresso.onView(ViewMatchers.withId(R.id.infoTextThirdLine))
        .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
  }

  protected fun verifyOnlyErrorLayoutIsVisibleAfterDelay(delay: Long){
    Thread.sleep(delay)
    Espresso.onView(ViewMatchers.withId(R.id.errorInfoRelativeLayout))
        .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.infoImageView))
        .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.infoTextFirstLine))
        .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.infoTextSecondLine))
        .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.infoTextThirdLine))
        .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
    Espresso.onView(ViewMatchers.withId(R.id.spinkitView))
        .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
    Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
        .check(ViewAssertions.matches(CoreMatchers.not(ViewMatchers.isDisplayed())))
  }*/
}