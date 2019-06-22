package zebrostudio.wallr100

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.utils.FragmentTag.*
import zebrostudio.wallr100.data.PREMIUM_USER_TAG
import zebrostudio.wallr100.data.PURCHASE_PREFERENCE_NAME
import zebrostudio.wallr100.data.SharedPrefsHelperImpl
import java.util.concurrent.TimeUnit

class MainActivityTest {

  @get:Rule val activityTestRule = ActivityTestRule(MainActivity::class.java)

  @Test fun shouldShowGuillotineMenuOnHamburgerClick() {
    onView(withId(R.id.contentHamburger))
        .perform(click())
        .check(matches(isCompletelyDisplayed()))
  }

  @Test fun shouldCloseGuillotineMenuOnHamburgerClickTwice() {
    onView(withId(R.id.contentHamburger))
        .perform(click())

    onView(withId(R.id.hamburgerGuillotineMenu))
        .perform(click())
        .check(matches(not(isCompletelyDisplayed())))
  }

  @Test fun shouldShowBuyProOptionInGuillotineMenu() {
    InstrumentationRegistry.getInstrumentation()
        .targetContext.let {
      SharedPrefsHelperImpl(it).setBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, false)
    }
    activityTestRule.launchActivity(Intent())


    onView(withId(R.id.contentHamburger))
        .perform(click())

    onView(withId(R.string.buy_pro_title))
        .check(matches(isCompletelyDisplayed()))
  }

  @Test fun shouldHideBuyProOptionInGuillotineMenu() {
    InstrumentationRegistry.getInstrumentation()
        .targetContext.let {
      SharedPrefsHelperImpl(it).setBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, true)
    }
    activityTestRule.launchActivity(Intent())

    onView(withId(R.id.contentHamburger))
        .perform(click())

    onView(withId(R.string.buy_pro_title))
        .check(matches(not(isCompletelyDisplayed())))
  }

  @Test fun shouldShowExitConfirmationMessageOnSingleBackPress() {
    onView(isRoot()).perform(pressBack())

    onView(withText(R.string.main_activity_exit_confirmation_message))
        .inRoot(withDecorView(not(activityTestRule.activity.window.decorView)))
        .check(matches(isDisplayed()))
  }

  @Test fun shouldExitAppOnDoubleBackPress() {
    onView(isRoot()).perform(pressBack())

    onView(withText(R.string.main_activity_exit_confirmation_message))
        .inRoot(withDecorView(not(activityTestRule.activity.window.decorView)))
        .check(matches(isDisplayed()))

    onView(isRoot()).perform(pressBackUnconditionally())

    assertTrue(activityTestRule.activity.isDestroyed)
  }

  @Test fun shouldShowExitConfirmationMessageOnDelayedDoubleBackPress() {
    onView(isRoot()).perform(pressBack())

    onView(withText(R.string.main_activity_exit_confirmation_message))
        .inRoot(withDecorView(not(activityTestRule.activity.window.decorView)))
        .check(matches(isDisplayed()))

    Thread.sleep(TimeUnit.SECONDS.toMillis(2))

    onView(isRoot()).perform(pressBack())

    onView(withText(R.string.main_activity_exit_confirmation_message))
        .inRoot(withDecorView(not(activityTestRule.activity.window.decorView)))
        .check(matches(isDisplayed()))

    assertTrue(!activityTestRule.activity.isFinishing)
    assertTrue(!activityTestRule.activity.isDestroyed)
  }

  @Test fun shouldShowExploreFragmentByDefaultWhenAppOpens() {
    onView(withId(R.id.toolbarTitle)).check(matches(
      withText(activityTestRule.activity.fragmentNameTagFetcher.getFragmentName(EXPLORE_TAG))))

    activityTestRule.activity.getFragmentTagAtStackTop().let {
      assertEquals(EXPLORE_TAG, it)
    }
  }

  @Test fun shouldShowExploreFragmentOnExploreMenuItemClick() {
    onView(withId(R.id.contentHamburger)).perform(click())

    onView(withId(R.string.explore_title)).perform(click())

    onView(withId(R.id.toolbarTitle)).check(matches(
      withText(activityTestRule.activity.fragmentNameTagFetcher.getFragmentName(EXPLORE_TAG))))

    activityTestRule.activity.getFragmentTagAtStackTop().let {
      assertEquals(EXPLORE_TAG, it)
    }
  }

  @Test fun shouldShowTopPicksFragmentOnTopPicksMenuItemClick() {
    onView(withId(R.id.contentHamburger)).perform(click())

    onView(withId(R.string.top_picks_title)).perform(click())

    onView(withId(R.id.toolbarTitle)).check(matches(
      withText(activityTestRule.activity.fragmentNameTagFetcher.getFragmentName(TOP_PICKS_TAG))))

    activityTestRule.activity.getFragmentTagAtStackTop().let {
      assertEquals(TOP_PICKS_TAG, it)
    }
  }

  @Test fun shouldShowCategoriesFragmentOnCategoriesMenuItemClick() {
    onView(withId(R.id.contentHamburger)).perform(click())

    onView(withId(R.string.categories_title)).perform(click())

    onView(withId(R.id.toolbarTitle)).check(matches(
      withText(activityTestRule.activity.fragmentNameTagFetcher.getFragmentName(CATEGORIES_TAG))))

    activityTestRule.activity.getFragmentTagAtStackTop().let {
      assertEquals(CATEGORIES_TAG, it)
    }
  }

  @Test fun shouldShowCollectionsFragmentOnCollectionsMenuItemClick() {
    onView(withId(R.id.contentHamburger)).perform(click())

    onView(withId(R.string.collection_title)).perform(click())

    onView(withId(R.id.toolbarTitle)).check(matches(
      withText(activityTestRule.activity.fragmentNameTagFetcher.getFragmentName(COLLECTIONS_TAG))))

    activityTestRule.activity.getFragmentTagAtStackTop().let {
      assertEquals(COLLECTIONS_TAG, it)
    }
  }
}