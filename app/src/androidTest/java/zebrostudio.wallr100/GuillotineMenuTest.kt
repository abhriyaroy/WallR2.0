package zebrostudio.wallr100

import android.Manifest
import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.Intents.intending
import android.support.test.espresso.intent.matcher.BundleMatchers
import android.support.test.espresso.intent.matcher.IntentMatchers.*
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.rule.GrantPermissionRule.grant
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.android.utils.FragmentTag.*
import zebrostudio.wallr100.data.PREMIUM_USER_TAG
import zebrostudio.wallr100.data.PURCHASE_PREFERENCE_NAME
import zebrostudio.wallr100.data.SharedPrefsHelperImpl
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class GuillotineMenuTest {

  @get:Rule val activityTestRule = ActivityTestRule(MainActivity::class.java)
  @get:Rule val grantPermissionRule: GrantPermissionRule =
      grant(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

  @Test fun should_show_guillotine_menu_on_hamburger_click() {
    onView(withId(R.id.contentHamburger))
        .perform(click())
        .check(matches(isCompletelyDisplayed()))
  }

  @Test fun should_close_guillotine_menu_on_hamburger_click_twice() {
    onView(withId(R.id.contentHamburger))
        .perform(click())

    onView(withId(R.id.hamburgerGuillotineMenu))
        .perform(click())
        .check(matches(not(isCompletelyDisplayed())))
  }

  @Test fun should_show_buy_pro_option_in_guillotine_menu_when_user_is_pro() {
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

  @Test fun should_hide_buy_pro_option_in_guillotine_menu_when_user_is_non_pro() {
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

  @Test fun should_show_exit_confirmation_message() {
    onView(isRoot()).perform(pressBack())

    onView(withText(R.string.main_activity_exit_confirmation_message))
        .inRoot(withDecorView(not(activityTestRule.activity.window.decorView)))
        .check(matches(isDisplayed()))
  }

  @Test fun should_close_app_after_exit_confirmation() {
    onView(isRoot()).perform(pressBack())

    onView(withText(R.string.main_activity_exit_confirmation_message))
        .inRoot(withDecorView(not(activityTestRule.activity.window.decorView)))
        .check(matches(isDisplayed()))

    onView(isRoot()).perform(pressBackUnconditionally())

    assertTrue(activityTestRule.activity.isDestroyed)
  }

  @Test fun should_show_exit_confirmation_message_on_delayed_double_back_press() {
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

  @Test fun should_show_collections_fragment_when_app_opens() {
    onView(withId(R.id.toolbarTitle)).check(matches(
      withText(activityTestRule.activity.fragmentNameTagFetcher.getFragmentName(EXPLORE_TAG))))

    activityTestRule.activity.getFragmentTagAtStackTop().let {
      assertEquals(EXPLORE_TAG, it)
    }
  }

  @Test fun should_show_explore_fragment_on_explore_menu_item_click() {
    onView(withId(R.id.contentHamburger)).perform(click())

    onView(withId(R.string.explore_title)).perform(click())

    onView(withId(R.id.toolbarTitle)).check(matches(
      withText(activityTestRule.activity.fragmentNameTagFetcher.getFragmentName(EXPLORE_TAG))))

    activityTestRule.activity.getFragmentTagAtStackTop().let {
      assertEquals(EXPLORE_TAG, it)
    }
  }

  @Test fun should_show_top_picks_fragment_on_top_picks_menu_item_click() {
    onView(withId(R.id.contentHamburger)).perform(click())

    onView(withId(R.string.top_picks_title)).perform(click())

    onView(withId(R.id.toolbarTitle)).check(matches(
      withText(activityTestRule.activity.fragmentNameTagFetcher.getFragmentName(TOP_PICKS_TAG))))

    activityTestRule.activity.getFragmentTagAtStackTop().let {
      assertEquals(TOP_PICKS_TAG, it)
    }
  }

  @Test fun should_show_catergoris_fragment_on_categories_menu_item_click() {
    onView(withId(R.id.contentHamburger)).perform(click())

    onView(withId(R.string.categories_title)).perform(click())

    onView(withId(R.id.toolbarTitle)).check(matches(
      withText(activityTestRule.activity.fragmentNameTagFetcher.getFragmentName(CATEGORIES_TAG))))

    activityTestRule.activity.getFragmentTagAtStackTop().let {
      assertEquals(CATEGORIES_TAG, it)
    }
  }

  @Test fun should_show_collections_fragment_on_collections_menu_item_click() {
    InstrumentationRegistry.getInstrumentation()
        .targetContext.let {
      SharedPrefsHelperImpl(it).setBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, true)
    }
    activityTestRule.launchActivity(Intent())

    onView(withId(R.id.contentHamburger)).perform(click())

    onView(withId(R.string.collection_title)).perform(click())

    onView(withId(R.id.toolbarTitle)).check(matches(
      withText(activityTestRule.activity.fragmentNameTagFetcher.getFragmentName(COLLECTIONS_TAG))))

    activityTestRule.activity.getFragmentTagAtStackTop().let {
      assertEquals(COLLECTIONS_TAG, it)
    }
  }

  @Test fun should_show_feedback_client_on_feedback_menu_item_click() {
    Intents.init()
    intending(allOf(hasAction(ACTION_CHOOSER)))
        .respondWith(Instrumentation.ActivityResult(Activity.RESULT_OK, Intent()))

    onView(withId(R.id.contentHamburger)).perform(click())

    onView(withId(R.string.feedback_title)).perform(click())

    Bundle().apply {

    }

    intended(allOf(hasAction(ACTION_CHOOSER),
      hasExtras(BundleMatchers.hasValue(allOf(hasAction(ACTION_SENDTO),
        hasData(Uri.parse("mailto:"))))),
      hasExtra(EXTRA_TITLE, "Contact using")))

    Intents.release()
  }

  @Test fun should_show_pro_badge_in_guillotine_menu_when_user_is_pro() {
    InstrumentationRegistry.getInstrumentation()
        .targetContext.let {
      SharedPrefsHelperImpl(it).setBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, true)
    }
    activityTestRule.launchActivity(Intent())

    onView(withId(R.id.contentHamburger))
        .perform(click())

    onView(withId(R.id.proBadgeGuillotineMenu))
        .check(matches(isCompletelyDisplayed()))
  }

  @Test fun should_hide_pro_badge_in_guillotine_menu_when_user_is_not_pro() {
    InstrumentationRegistry.getInstrumentation()
        .targetContext.let {
      SharedPrefsHelperImpl(it).setBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, false)
    }
    activityTestRule.launchActivity(Intent())

    onView(withId(R.id.contentHamburger))
        .perform(click())

    onView(withId(R.id.proBadgeGuillotineMenu))
        .check(matches(not(isCompletelyDisplayed())))
  }
}