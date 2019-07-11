package zebrostudio.wallr100.tests.imagelistfragments

import android.graphics.Color.parseColor
import android.graphics.drawable.ColorDrawable
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.ImageView
import io.reactivex.Single
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.adapters.ViewHolder
import zebrostudio.wallr100.android.utils.FragmentTag.*


@RunWith(AndroidJUnit4::class)
class MinimalFragmentTest : BaseImageListFragmentTest() {

  private val mockColorList = arrayListOf(
      "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5", "#2196F3", "#03A9F4", "#00BCD4",
      "#009688", "#4CAF50", "#8BC34A", "#CDDC39", "#FFEB3B", "#FFC107", "#FF9800", "#FF5722",
      "#795548", "#9E9E9E", "#607D8B", "#000000", "#89798F", "#516172", "#687685", "#709094",
      "#9BA9AE", "#4B3A37", "#9C6534"
  )

  @Before
  fun setup() {
    tagPrefix = "$MINIMAL_TAG"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_show_default_colors_list() {
    `when`(mockWallrRepository.isCustomMinimalColorListPresent()).thenReturn(false)
    `when`(mockWallrRepository.getDefaultMinimalColorList()).thenReturn(Single.just(mockColorList))
    openMinimalFragment()
    onView(withId(R.id.minimalFragmentRecyclerView))
      .check(matches(shouldShowAddColorIcon()))

    for (index in 0 until mockColorList.size) {
      val position = index+1
      onView(withId(R.id.minimalFragmentRecyclerView))
        .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(position))
        .check(matches(
            shouldShowImageViewWithBackgroundColorInRecyclerView(position, mockColorList[index])))
    }
  }

  @Test
  fun should_show_custom_colors_list() {
    `when`(mockWallrRepository.isCustomMinimalColorListPresent()).thenReturn(true)
    `when`(mockWallrRepository.getCustomMinimalColorList()).thenReturn(Single.just(mockColorList))
    openMinimalFragment()
    onView(withId(R.id.minimalFragmentRecyclerView))
      .check(matches(shouldShowAddColorIcon()))

    for (index in 0 until mockColorList.size) {
      val position = index+1
      onView(withId(R.id.minimalFragmentRecyclerView))
        .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(position))
        .check(matches(
            shouldShowImageViewWithBackgroundColorInRecyclerView(position, mockColorList[index])))
    }
  }

  private fun openMinimalFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(ViewActions.click())
    onView(withId(R.string.minimal_title)).perform(ViewActions.click())
  }

  private fun shouldShowAddColorIcon(): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
      override fun describeTo(description: Description?) {
        description?.appendText("with add icon tag at first index")
      }

      override fun matchesSafely(recycelrView: RecyclerView?): Boolean {
        val viewHolder = recycelrView?.findViewHolderForAdapterPosition(0)
        with(viewHolder?.itemView?.findViewById<ImageView>(R.id.addColorIcon)) {
          if (this?.visibility == VISIBLE) {
            return true
          }
        }
        return false
      }
    }
  }

  private fun shouldShowImageViewWithBackgroundColorInRecyclerView(position: Int,
    color: String): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
      override fun describeTo(description: Description?) {
        description?.appendText("imageview with specified background not found")
      }

      override fun matchesSafely(recycelrView: RecyclerView?): Boolean {
        val viewHolder = recycelrView?.findViewHolderForAdapterPosition(position)
        with(viewHolder?.itemView?.findViewById<ImageView>(R.id.colorThumbnail)) {
          val backgroundColor = (this?.background as ColorDrawable).color
          if (this.visibility != VISIBLE || backgroundColor != parseColor(color)) {
            return false
          }
        }
        return true
      }
    }
  }
}