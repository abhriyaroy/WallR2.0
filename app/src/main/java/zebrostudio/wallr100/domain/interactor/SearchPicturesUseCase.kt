package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.model.searchpictures.SearchPicturesModel

interface SearchPicturesUseCase {
  fun buildUseCaseSingle(query: String): Single<List<SearchPicturesModel>>
}

class SearchPicturesInteractor(
  private val wallrRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) : SearchPicturesUseCase {

  override fun buildUseCaseSingle(query: String): Single<List<SearchPicturesModel>> {
    return wallrRepository.getPictures(query)
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
  }
}