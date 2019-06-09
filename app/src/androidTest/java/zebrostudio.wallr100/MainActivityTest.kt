package zebrostudio.wallr100

<<<<<<< HEAD
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.isRoot
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.data.PREMIUM_USER_TAG
import zebrostudio.wallr100.data.PURCHASE_PREFERENCE_NAME
import zebrostudio.wallr100.data.SharedPrefsHelperImpl
import java.util.concurrent.TimeUnit

class MainActivityTest {

  @get:Rule val activityTestRule = ActivityTestRule(MainActivity::class.java)
=======
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
>>>>>>> Test guillotine menu

  @Test fun shouldShowGuillotineMenuOnHamburgerClick() {
    onView(withId(R.id.contentHamburger))
        .perform(click())
<<<<<<< HEAD
        .check(matches(ViewMatchers.isCompletelyDisplayed()))
  }

  @Test fun shouldCloseGuillotineMenuOnHamburgerClickTwice() {
    onView(withId(R.id.contentHamburger))
        .perform(click())

    onView(withId(R.id.hamburgerGuillotineMenu))
        .perform(click())
        .check(matches(Matchers.not(ViewMatchers.isCompletelyDisplayed())))
  }

  @Test fun shouldShowBuyProOptionInGuillotineMenu() {
    InstrumentationRegistry.getInstrumentation().targetContext.let {
      SharedPrefsHelperImpl(it).setBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, false)
    }
    activityTestRule.launchActivity(Intent())


    onView(withId(R.id.contentHamburger))
        .perform(click())

    onView(withId(R.string.guillotine_buy_pro_title))
        .check(matches(ViewMatchers.isCompletelyDisplayed()))
  }

  @Test fun shouldHideBuyProOptionInGuillotineMenu() {
    InstrumentationRegistry.getInstrumentation().targetContext.let {
      SharedPrefsHelperImpl(it).setBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, true)
    }
    activityTestRule.launchActivity(Intent())

    onView(withId(R.id.contentHamburger))
        .perform(click())

    onView(withId(R.string.guillotine_buy_pro_title))
        .check(matches(Matchers.not(ViewMatchers.isCompletelyDisplayed())))
  }

  @Test fun shouldShowExitConfirmationMessageOnSingleBackPress() {
    onView(isRoot()).perform(ViewActions.pressBack())

    onView(withText(R.string.main_activity_exit_confirmation_message))
        .inRoot(withDecorView(not(activityTestRule.activity.window.decorView)))
        .check(matches(isDisplayed()))
  }

  @Test fun shouldExitAppOnDoubleBackPress() {
    onView(isRoot()).perform(ViewActions.pressBack())

    onView(withText(R.string.main_activity_exit_confirmation_message))
        .inRoot(withDecorView(not(activityTestRule.activity.window.decorView)))
        .check(matches(isDisplayed()))

    onView(isRoot()).perform(ViewActions.pressBackUnconditionally())

    assertTrue(activityTestRule.activity.isDestroyed)
  }

  @Test fun shouldShowExitConfirmationMessageOnDelayedDoubleBackPress() {
    onView(isRoot()).perform(ViewActions.pressBack())

    onView(withText(R.string.main_activity_exit_confirmation_message))
        .inRoot(withDecorView(not(activityTestRule.activity.window.decorView)))
        .check(matches(isDisplayed()))

    Thread.sleep(TimeUnit.SECONDS.toMillis(2))

    onView(isRoot()).perform(ViewActions.pressBackUnconditionally())

    assertTrue(!activityTestRule.activity.isFinishing)
    assertTrue(!activityTestRule.activity.isDestroyed)
=======
        .check(matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
>>>>>>> Test guillotine menu
  }
}