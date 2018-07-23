package zebrostudio.wallr100.android

interface BasePresenter<T> {

  fun attachView(view: T)

  fun detachView()

}