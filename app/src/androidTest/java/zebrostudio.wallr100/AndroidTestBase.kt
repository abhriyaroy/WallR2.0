package zebrostudio.wallr100

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.github.ybq.android.spinkit.SpinKitView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

abstract class AndroidTestBase {

  protected fun hasImageViewWithTagAtPosition(position: Int,
    imageViewId: Int,
    viewTag: String): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
      override fun describeTo(description: Description?) {
        description?.appendText("with Imageview tag at position $position")
      }

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

  fun matchViewByIdAndTag(expectedTag: String): Matcher<View> {
    return object : TypeSafeMatcher<View>() {
      override fun describeTo(description: Description) {
        description.appendText("with first child view of type parentMatcher")
      }

      override fun matchesSafely(view: View): Boolean {
        return (view.tag == expectedTag)
       /* Log.d("viewtagis", view.findViewById<SpinKitView>(id).tag.toString())
        if (expectedTag == view.findViewById<SpinKitView>(id).tag) {
          return true
        }*/
      }
    }
  }

}