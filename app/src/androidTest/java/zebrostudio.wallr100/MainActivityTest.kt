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
import zebrostudio.wallr100.di.TestAppComponent
import zebrostudio.wallr100.domain.WallrRepository
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
class MainActivityTest : TestBase {

  private val component = MockedRepositoryTestRule(InstrumentationRegistry.getTargetContext())

  val main = ActivityTestRule(MainActivity::class.java, false, false)

  @get: Rule
  var chain: TestRule = RuleChain.outerRule(component).around(main)

  @Inject
  internal lateinit var mockWallrRepository: WallrRepository


  /*@Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    val app = InstrumentationRegistry.getTargetContext().applicationContext as WallrApplication
    testAppComponent = DaggerTestAppComponent.builder()
        .application(app)
        .build()
    app.setAppComponent(testAppComponent)
    testAppComponent.inject(app)
    System.out.println("==== TestAppComponent injected")
  }*/

  @Test
  fun should_show_explore_images() {
    val a = 1
    mockWallrRepository = component.getTestAppComponent().wallrRepository
    `when`(mockWallrRepository.clearImageCaches()).thenReturn(Completable.complete())
  }
}