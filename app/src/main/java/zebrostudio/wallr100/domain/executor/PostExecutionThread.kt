package zebrostudio.wallr100.domain.executor

import io.reactivex.Scheduler

interface PostExecutionThread {
  val scheduler: Scheduler
}