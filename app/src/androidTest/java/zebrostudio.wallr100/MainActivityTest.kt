package zebrostudio.wallr100

import android.support.test.InstrumentationRegistry
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import zebrostudio.wallr100.android.WallrApplication
import zebrostudio.wallr100.android.ui.main.MainActivity
import zebrostudio.wallr100.di.DaggerTestAppComponent
import zebrostudio.wallr100.di.TestAppComponent

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  private val component = MockedRepositoryTestRule(InstrumentationRegistry.getTargetContext())
  @get:Rule
  val main = ActivityTestRule(MainActivity::class.java, false, false)

  /*@get: Rule
  var chain: TestRule = RuleChain.outerRule(component).around(main)*/

  private lateinit var testAppComponent: TestAppComponent

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    val app = InstrumentationRegistry.getTargetContext().applicationContext as WallrApplication
    testAppComponent = DaggerTestAppComponent.builder()
        .application(app)
        .build()
    app.setAppComponent(testAppComponent)
    testAppComponent.inject(app)
    System.out.println("==== TestAppComponent injected")
  }

  @Test
  fun test1() {
    val a = 1
  }
}