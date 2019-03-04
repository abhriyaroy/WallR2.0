package zebrostudio.wallr100.android

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.executor.ExecutionThread

class AndroidBackgroundThreads : ExecutionThread {

  override val ioScheduler: Scheduler
    get() = Schedulers.io()

  override val computationScheduler: Scheduler
    get() = Schedulers.computation()
}