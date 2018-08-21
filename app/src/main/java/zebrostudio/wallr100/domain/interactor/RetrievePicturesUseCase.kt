package zebrostudio.wallr100.domain.interactor

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.model.PicturesModel

class RetrievePicturesUseCase(
  private val wallrRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) {

  fun builRetrievePicturesObservable(query: String): Observable<PicturesModel> {
    return wallrRepository.getPictures(query)
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
  }
}