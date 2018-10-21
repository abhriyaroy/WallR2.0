package zebrostudio.wallr100.presentation

interface BasePresenter<T> {

  fun attachView(view: T)

  fun detachView()

}