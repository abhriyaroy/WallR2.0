package zebrostudio.wallr100.domain.interactor

import android.graphics.Bitmap
import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Single
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType

interface ColorImagesUseCase {
  fun getSingularColorBitmapSingle(colorHex: String): Single<Bitmap>
  fun getMultiColorBitmapSingle(
    colorHexList: List<String>,
    multiColorImageType: MultiColorImageType
  ): Single<Bitmap>

  fun getBitmapSingle(): Single<Bitmap>
  fun getBitmapFromUriSingle(uri: Uri?): Single<Bitmap>
  fun saveToCollectionsCompletable(
    data: String,
    type: CollectionsImageType
  ): Completable

  fun getCroppingSourceUri(): Single<Uri>
  fun getCroppingDestinationUri(): Single<Uri>
  fun getCacheImageUri(): Single<Uri>
  fun downloadImage(): Completable
  fun clearCachesCompletable(): Completable
}

class ColorImagesInteractor(private val wallrRepository: WallrRepository) : ColorImagesUseCase {

  override fun getSingularColorBitmapSingle(colorHex: String): Single<Bitmap> {
    return wallrRepository.getSingleColorBitmap(colorHex)
  }

  override fun getMultiColorBitmapSingle(
    colorHexList: List<String>,
    multiColorImageType: MultiColorImageType
  ): Single<Bitmap> {
    return wallrRepository.getMultiColorBitmap(colorHexList, multiColorImageType)
  }

  override fun getBitmapSingle(): Single<Bitmap> {
    return wallrRepository.getImageBitmap()
  }

  override fun getBitmapFromUriSingle(uri: Uri?): Single<Bitmap> {
    return wallrRepository.getBitmapFromUri(uri)
  }

  override fun saveToCollectionsCompletable(
    data: String,
    type: CollectionsImageType
  ) = wallrRepository.saveImageToCollections(data, type)

  override fun getCroppingSourceUri() = wallrRepository.getCacheSourceUri()

  override fun getCroppingDestinationUri() = wallrRepository.getCacheResultUri()

  override fun getCacheImageUri() = wallrRepository.getShareableImageUri()

  override fun downloadImage() = wallrRepository.saveCachedImageToDownloads()

  override fun clearCachesCompletable(): Completable {
    return wallrRepository.clearImageCaches()
  }

}