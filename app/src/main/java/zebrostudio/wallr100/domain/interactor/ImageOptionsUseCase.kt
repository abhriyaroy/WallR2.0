package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel

interface ImageOptionsUseCase {
  fun fetchImageBitmapObservable(link: String): Observable<ImageDownloadModel>
  fun getImageShareableLinkSingle(link: String): Single<String>
  fun clearCachesCompletable(): Completable
  fun cancelFetchImageOperation()
  fun getCroppingSourceUri(): Uri
  fun getCroppingDestinationUri(): Uri
  fun getEditedImageSingle(): Single<Bitmap>
  fun crystallizeImageSingle(): Single<Pair<Boolean, Bitmap>>
  fun getBitmapFromUriSingle(imageUri: Uri?): Single<Bitmap>
  fun downloadImageCompletable(link: String): Completable
  fun downloadCrystallizedImageCompletable(): Completable
  fun isDownloadInProgress(link: String): Boolean
  fun isCrystallizeDescriptionDialogShown(): Boolean
  fun setCrystallizeDescriptionShownOnce()
  fun getCrystallizedImageSingle(): Single<Bitmap>
  fun addImageToCollection(data: String, type: CollectionsImageType): Completable
}

class ImageOptionsInteractor(
  private var wallrRepository: WallrRepository
) : ImageOptionsUseCase {

  override fun fetchImageBitmapObservable(link: String): Observable<ImageDownloadModel> {
    return wallrRepository.getImageBitmap(link)
  }

  override fun getImageShareableLinkSingle(link: String): Single<String> {
    return wallrRepository.getShortImageLink(link)
  }

  override fun clearCachesCompletable(): Completable {
    return wallrRepository.clearImageCaches()
  }

  override fun cancelFetchImageOperation() {
    wallrRepository.cancelImageBitmapFetchOperation()
  }

  override fun getCroppingSourceUri() = wallrRepository.getCacheSourceUri()

  override fun getCroppingDestinationUri() = wallrRepository.getCacheResultUri()

  override fun getEditedImageSingle(): Single<Bitmap> {
    return wallrRepository.getCacheImageBitmap()
  }

  override fun getBitmapFromUriSingle(imageUri: Uri?): Single<Bitmap> {
    return wallrRepository.getBitmapFromUri(imageUri)
  }

  override fun crystallizeImageSingle(): Single<Pair<Boolean, Bitmap>> {
    return wallrRepository.crystallizeImage()
  }

  override fun downloadImageCompletable(link: String): Completable {
    return wallrRepository.downloadImage(link)
  }

  override fun downloadCrystallizedImageCompletable(): Completable {
    return wallrRepository.saveCachedImageToDownloads()
  }

  override fun isDownloadInProgress(link: String): Boolean {
    return wallrRepository.checkIfDownloadIsInProgress(link)
  }

  override fun isCrystallizeDescriptionDialogShown(): Boolean {
    return wallrRepository.isCrystallizeDescriptionShown()
  }

  override fun setCrystallizeDescriptionShownOnce() {
    wallrRepository.saveCrystallizeDescriptionShown()
  }

  override fun getCrystallizedImageSingle(): Single<Bitmap> {
    return wallrRepository.getCacheImageBitmap()
  }

  override fun addImageToCollection(data: String, type: CollectionsImageType): Completable {
    return wallrRepository.saveImageToCollections(data, type)
  }

}