package zebrostudio.wallr100

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import zebrostudio.wallr100.android.ui.main.MainActivity

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  @Rule val activityTestRule = ActivityTestRule(MainActivity::class.java)

  @Test fun shouldShowGuillotineMenuOnHamburgerClick() {
    onView(withId(R.id.contentHamburger))
        .perform(click())
        .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
  }
}