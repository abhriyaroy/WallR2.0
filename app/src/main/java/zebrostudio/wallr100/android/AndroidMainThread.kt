package zebrostudio.wallr100.android

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import org.buffer.android.boilerplate.domain.executor.PostExecutionThread

class AndroidMainThread : PostExecutionThread {

  override val scheduler: Scheduler
    get() = AndroidSchedulers.mainThread()

}