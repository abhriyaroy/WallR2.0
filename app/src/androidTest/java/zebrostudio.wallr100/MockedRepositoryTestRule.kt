package zebrostudio.wallr100

import android.content.Context
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import zebrostudio.wallr100.android.WallrApplication
import zebrostudio.wallr100.di.DaggerTestAppComponent
import zebrostudio.wallr100.di.TestAppComponent

class MockedRepositoryTestRule(private var appContext: Context) : TestRule {

  private var testAppComponent: TestAppComponent? = null

  override fun apply(base: Statement, description: Description): Statement {
    return object : Statement() {
      override fun evaluate() {
        try {
          setupDaggerTestComponentInApplication()
          base.evaluate()
        } finally {
          testAppComponent = null
        }
      }

    }
  }

  fun getTestAppComponent(): TestAppComponent = testAppComponent!!

  private fun setupDaggerTestComponentInApplication() {
    val application = WallrApplication.getApplication(appContext)
    testAppComponent = DaggerTestAppComponent.builder()
        .application(application)
        .build()
    application.setAppComponent(testAppComponent!!)
  }
}