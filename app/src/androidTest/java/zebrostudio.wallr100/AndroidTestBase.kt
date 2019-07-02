package zebrostudio.wallr100

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import org.hamcrest.Description
import org.hamcrest.Matcher

open class AndroidTestBase {

  protected fun hasImageViewWithTagAtPosition(position: Int,
    imageViewId: Int,
    viewTag: String): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
      override fun describeTo(description: Description?) {}

      override fun matchesSafely(recycelrView: RecyclerView?): Boolean {
        val viewHolder = recycelrView?.findViewHolderForAdapterPosition(position)
        with(viewHolder?.itemView?.findViewById<ImageView>(imageViewId)) {
          if (this?.tag?.equals(viewTag) == true) {
            return true
          }
        }
        return false
      }

    }
  }

}