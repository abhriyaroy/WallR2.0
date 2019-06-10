package zebrostudio.wallr100.rules

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TrampolineSchedulerRule : TestRule {

  override fun apply(base: Statement, d: Description): Statement {
    return object : Statement() {
      @Throws(Throwable::class)
      override fun evaluate() {
        RxJavaPlugins.setIoSchedulerHandler { _ -> Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { _ -> Schedulers.trampoline() }
        RxJavaPlugins.setNewThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.trampoline() }
        try {
          base.evaluate()
        } finally {
          RxJavaPlugins.reset()
          RxAndroidPlugins.reset()
        }
      }
    }
  }

}