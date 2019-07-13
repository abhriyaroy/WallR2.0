package zebrostudio.wallr100.tests.imagelistfragments

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.*
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.GrantPermissionRule
import android.support.test.rule.GrantPermissionRule.grant
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView.*
import io.reactivex.Single
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.utils.FragmentTag.COLLECTIONS_TAG
import zebrostudio.wallr100.android.utils.stringRes
import zebrostudio.wallr100.dummylists.collectionimages.MockCollectionsImageModelList
import zebrostudio.wallr100.dummylists.collectionimages.MockCollectionsImageModelList.getMultipleImagesList
import zebrostudio.wallr100.dummylists.collectionimages.MockCollectionsImageModelList.getSingleImageList

@RunWith(AndroidJUnit4::class)
class CollectionsFragmentTest : BaseImageListFragmentTest() {

  private val grantPermissionRule: GrantPermissionRule =
      grant(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
  @get: Rule
  override var ruleChain: TestRule =
      RuleChain.outerRule(testComponentRule).around(grantPermissionRule).around(activityTestRule)

  @Before
  fun setup() {
    tagPrefix = "$COLLECTIONS_TAG"
    initMocks()
    `when`(mockWallrRepository.getExplorePictures()).thenReturn(Single.error(Exception()))
  }

  @Test
  fun should_show_buy_pro_dialog_when_user_is_not_pro() {
    `when`(mockWallrRepository.getImagesInCollection()).thenReturn(Single.just(emptyList()))
    openCollectionsFragment()
    onView(
        withText(getTargetContext().stringRes(R.string.collections_fragment_purchase_pro_diloag_title)))
      .check(matches(isCompletelyDisplayed()))
    onView(
        withText(getTargetContext().stringRes(R.string.collections_fragment_purchase_pro_diloag_description)))
      .check(matches(isCompletelyDisplayed()))
    onView(
        withText(getTargetContext().stringRes(R.string.collections_fragment_purchase_pro_diloag_positive_text)))
      .check(matches(isCompletelyDisplayed()))
    onView(
        withText(getTargetContext().stringRes(R.string.collections_fragment_purchase_pro_diloag_negative_text)))
      .check(matches(isCompletelyDisplayed()))
  }

  @Test
  fun should_show_empty_collection_view() {
    `when`(mockWallrRepository.isUserPremium()).thenReturn(true)
    `when`(mockWallrRepository.getImagesInCollection()).thenReturn(Single.just(emptyList()))
    openCollectionsFragment()

    onView(withId(R.id.switchLayout)).check(matches(not(isDisplayed())))
    onView(withId(R.id.emptyCollectionImageView)).check(matches(isCompletelyDisplayed()))
    onView(withId(R.id.emptyCollectionTextView)).check(matches(isCompletelyDisplayed()))
    onView(withId(R.id.emptyCollectionSubtext)).check(matches(isCompletelyDisplayed()))
  }

  @Test
  fun should_show_multiple_images_in_collection_view_with_switch_layout() {
    val imageList = getMultipleImagesList()
    `when`(mockWallrRepository.isUserPremium()).thenReturn(true)
    `when`(mockWallrRepository.isCollectionReorderHintDisplayedBefore()).thenReturn(true)
    `when`(mockWallrRepository.getImagesInCollection()).thenReturn(Single.just(imageList))
    openCollectionsFragment()

    onView(withId(R.id.switchLayout)).check(matches(isDisplayed()))
    verifyEmptyCollectionViewNotShown()
    for ((index, collectionImageModel) in imageList.withIndex()) {
      onView(withId(R.id.collectionsRecyclerView))
        .perform(RecyclerViewActions.scrollToPosition<ViewHolder>(index))
        .check(matches(
            hasImageViewWithTagInRecyclerView(index, R.id.imageView, collectionImageModel.path)))
    }
  }

  @Test
  fun should_show_single_image_in_collection_view_without_switch_layout() {
    val imageList = getSingleImageList()
    `when`(mockWallrRepository.isUserPremium()).thenReturn(true)
    `when`(mockWallrRepository.getImagesInCollection()).thenReturn(Single.just(imageList))
    openCollectionsFragment()

    onView(withId(R.id.switchLayout)).check(matches(not(isDisplayed())))
    verifyEmptyCollectionViewNotShown()
    onView(withId(R.id.collectionsRecyclerView))
      .check(matches(
          hasImageViewWithTagInRecyclerView(0, R.id.imageView, imageList[0].path)))
  }

  private fun openCollectionsFragment() {
    activityTestRule.launchActivity(null)
    onView(withId(R.id.contentHamburger)).perform(click())
    onView(withId(R.string.collection_title)).perform(click())
  }

  private fun verifyEmptyCollectionViewNotShown() {
    onView(withId(R.id.emptyCollectionImageView)).check(matches(not(isDisplayed())))
    onView(withId(R.id.emptyCollectionTextView)).check(matches(not(isDisplayed())))
    onView(withId(R.id.emptyCollectionSubtext)).check(matches(not(isDisplayed())))
  }

}