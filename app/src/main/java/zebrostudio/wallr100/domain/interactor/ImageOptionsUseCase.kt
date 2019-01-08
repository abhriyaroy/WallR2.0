package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread

interface ImageOptionsUseCase {
  fun getImageShareableLinkSingle(link: String): Single<String>
}

class ImageOptionsInteractor(
  private var wallrRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) : ImageOptionsUseCase {

  override fun getImageShareableLinkSingle(link: String): Single<String> {
    return wallrRepository.getShortImageLink(link)
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
  }

}