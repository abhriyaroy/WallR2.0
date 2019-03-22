package zebrostudio.wallr100.android.test

import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit

class Repo {

  fun getSingle(): Single<String> {
    return Single.fromCallable {
      println("Called")
      getData()
    }
  }

  fun getSingle3(): Single<String> {
    return Single.create {
      println("Called")

      getData2(object : Callback {
        override fun onData(value: String) {
          it.onSuccess(value)
        }

        override fun onError(throwable: Throwable) {
          it.onError(throwable)
        }
      })
      it.onSuccess(getData())
    }
  }

  fun getSingle2(): Single<CharSequence> {
    return Single.fromCallable(object : Callable<CharSequence> {
      override fun call(): CharSequence {
        return getData()
      }
    })
  }

  fun getData(): String {
    throw IllegalStateException("Boom")
    return "Hello World"
  }

  fun getData2(callback: Callback) {
    callback.onData("Hello World")
  }

  fun getConnectableObservable() {
    val connectableObservable =
        Observable.interval(1000, TimeUnit.MILLISECONDS).publish()
    val observable = connectableObservable.connect()

    Thread.sleep(2000)

    val s1 = connectableObservable.subscribe {
      println("first $it")
    }

    Thread.sleep(2000)


    val s2 = connectableObservable.subscribe {
      println("second $it")
    }

    Thread.sleep(2000)

    s1.dispose()

    Thread.sleep(2000)

    observable.dispose()

    Thread.sleep(10000)

  }
}

interface Callback {
  fun onData(value: String)
  fun onError(throwable: Throwable)
}