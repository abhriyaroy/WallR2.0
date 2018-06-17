package zebrostudio.wallr100

interface BasePresenter<T> {

  fun attachView(view: T)
  fun detachView()
  
}