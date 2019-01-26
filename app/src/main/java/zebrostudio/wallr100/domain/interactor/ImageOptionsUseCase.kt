package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel

interface ImageOptionsUseCase {
  fun fetchImageBitmapObservable(link: String): Observable<ImageDownloadModel>
  fun getImageShareableLinkSingle(link: String): Single<String>
  fun clearCachesCompletable(): Completable
  fun cancelFetchImageOperation()
  fun getCroppingSourceUri(): Uri
  fun getCroppingDestinationUri(): Uri
  fun getBitmapFromUriSingle(imageUri: Uri): Single<Bitmap>
}

class ImageOptionsInteractor(
  private var wallrRepository: WallrRepository,
  private val postExecutionThread: PostExecutionThread
) : ImageOptionsUseCase {

  override fun fetchImageBitmapObservable(link: String): Observable<ImageDownloadModel> {
    return wallrRepository.getImageBitmap(link)
        .subscribeOn(Schedulers.io())
  }

  override fun getImageShareableLinkSingle(link: String): Single<String> {
    return wallrRepository.getShortImageLink(link)
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
  }

  override fun clearCachesCompletable(): Completable {
    return wallrRepository.clearImageCaches()
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
  }

  override fun cancelFetchImageOperation() {
    wallrRepository.cancelImageBitmapFetchOperation()
  }

  override fun getCroppingSourceUri() = wallrRepository.getCacheSourceUri()

  override fun getCroppingDestinationUri() = wallrRepository.getCacheResultUri()

  override fun getBitmapFromUriSingle(imageUri: Uri): Single<Bitmap> {
    return wallrRepository.getBitmapFromUri(imageUri)
        .subscribeOn(Schedulers.io())
  }

}