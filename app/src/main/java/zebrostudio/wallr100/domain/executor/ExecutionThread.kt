package zebrostudio.wallr100.domain.executor

import io.reactivex.Scheduler

interface ExecutionThread {
  val ioScheduler: Scheduler
  val computationScheduler: Scheduler
}