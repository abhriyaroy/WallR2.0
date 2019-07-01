package zebrostudio.wallr100

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.reactivex.Completable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.domain.WallrRepository
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestBaseClass() {

  private val testComponentRule = MockedRepositoryTestRule(InstrumentationRegistry.getTargetContext())
  private val activityTestRule = ActivityTestRule(MainActivity::class.java, false, false)

  @get: Rule
  var ruleChain: TestRule = RuleChain.outerRule(testComponentRule).around(activityTestRule)

  @Inject
  internal lateinit var mockWallrRepository: WallrRepository

  @Before
  fun setup() {
    mockWallrRepository = testComponentRule.getTestAppComponent().wallrRepository
  }

  @Test
  fun should_show_explore_images() {
    val a = 1
    `when`(mockWallrRepository.clearImageCaches()).thenReturn(Completable.complete())
  }
}