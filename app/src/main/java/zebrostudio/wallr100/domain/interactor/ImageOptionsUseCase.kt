package zebrostudio.wallr100.domain.interactor

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread

interface ShareImagesUseCase {
  fun getImageShareableLink(link: String): Single<String>
}

class ShareImagesInteractor(
  private var wallrRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) : ShareImagesUseCase {

  override fun getImageShareableLink(link: String): Single<String> {
    return wallrRepository.getShortImageLink(link)
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
  }

}