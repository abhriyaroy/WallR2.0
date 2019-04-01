package zebrostudio.wallr100.data

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.pddstudio.urlshortener.URLShortener
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.exception.EmptyRecentlyDeletedMapException
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.NotEnoughFreeSpaceException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapper
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapper
import zebrostudio.wallr100.data.model.firebasedatabase.FirebaseImageEntity
import zebrostudio.wallr100.domain.WallrRepository
import zebrostudio.wallr100.domain.executor.ExecutionThread
import zebrostudio.wallr100.domain.model.RestoreColorsModel
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel
import zebrostudio.wallr100.domain.model.images.ImageModel
import zebrostudio.wallr100.domain.model.searchpictures.SearchPicturesModel
import java.util.Collections
import java.util.TreeMap
import java.util.concurrent.TimeUnit.SECONDS

const val SUCCESS_STATUS = "success"
const val ERROR_STATUS = "error"
const val PURCHASE_PREFERENCE_NAME = "PURCHASE_PREF"
const val PREMIUM_USER_TAG = "premium_user"
const val IMAGE_PREFERENCE_NAME = "IMAGE_PREF"
const val CRYSTALLIZE_HINT_DIALOG_SHOWN_BEFORE_TAG = "crystallize_click_dialog"
const val CUSTOM_MINIMAL_COLOR_LIST_AVAILABLE_TAG = "custom_minimal_color_list_availability"
const val CUSTOM_MINIMAL_COLOR_LIST_TAG = "custom_solid_color_list"
const val UNABLE_TO_RESOLVE_HOST_EXCEPTION_MESSAGE = "Unable to resolve host " +
    "\"api.unsplash.com\": No address associated with hostname"
const val FIREBASE_DATABASE_PATH = "wallr"
const val CHILD_PATH_EXPLORE = "explore"
const val CHILD_PATH_CATEGORIES = "categories"
const val CHILD_PATH_TOP_PICKS = "collections"
const val CHILD_PATH_RECENT = "recent"
const val CHILD_PATH_POPULAR = "popular"
const val CHILD_PATH_STANDOUT = "standout"
const val CHILD_PATH_BUILDING = "building"
const val CHILD_PATH_FOOD = "food"
const val CHILD_PATH_NATURE = "nature"
const val CHILD_PATH_OBJECT = "object"
const val CHILD_PATH_PEOPLE = "people"
const val CHILD_PATH_TECHNOLOGY = "technology"
const val FIREBASE_TIMEOUT_DURATION = 15
const val IMAGE_DOWNLOAD_FINISHED_VALUE: Long = 100
const val IMAGE_DOWNLOAD_PROGRESS_VALUE_99: Long = 99

class WallrDataRepository(
  private val retrofitFirebaseAuthFactory: RemoteAuthServiceFactory,
  private val unsplashClientFactory: UnsplashClientFactory,
  private val sharedPrefsHelper: SharedPrefsHelper,
  private val gsonDataHelper: GsonDataHelper,
  private val unsplashPictureEntityMapper: UnsplashPictureEntityMapper,
  private val firebaseDatabaseHelper: FirebaseDatabaseHelper,
  private val firebasePictureEntityMapper: FirebasePictureEntityMapper,
  private val urlShortener: URLShortener,
  private val imageHandler: ImageHandler,
  private val fileHandler: FileHandler,
  private val downloadHelper: DownloadHelper,
  private val minimalColorHelper: MinimalColorHelper,
  private val executionThread: ExecutionThread
) : WallrRepository {

  override fun authenticatePurchase(
    packageName: String,
    skuId: String,
    purchaseToken: String
  ): Completable {
    return Completable.fromSingle(retrofitFirebaseAuthFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(packageName, skuId, purchaseToken))
        .subscribeOn(executionThread.ioScheduler)
        .flatMap {
          if (it.status == SUCCESS_STATUS) {
            Single.just(true)
          } else if (it.status == ERROR_STATUS && (it.errorCode == 404 || it.errorCode == 403)) {
            Single.error(InvalidPurchaseException())
          } else {
            Single.error(UnableToVerifyPurchaseException())
          }
        })
  }

  override fun updateUserPurchaseStatus() = sharedPrefsHelper.setBoolean(PURCHASE_PREFERENCE_NAME,
      PREMIUM_USER_TAG, true)

  override fun isUserPremium() = sharedPrefsHelper.getBoolean(PURCHASE_PREFERENCE_NAME,
      PREMIUM_USER_TAG, false)

  override fun getSearchPictures(query: String): Single<List<SearchPicturesModel>> {
    return unsplashClientFactory.getPicturesService(query)
        .subscribeOn(executionThread.ioScheduler)
        .flatMap {
          if (it.isEmpty()) {
            Single.error(NoResultFoundException())
          } else {
            val map = unsplashPictureEntityMapper.mapFromEntity(it)
            Single.just(map)
          }
        }
        .onErrorResumeNext {
          if (it.message != null && it.message == UNABLE_TO_RESOLVE_HOST_EXCEPTION_MESSAGE) {
            Single.error(UnableToResolveHostException())
          } else {
            Single.error(it)
          }
        }
  }

  override fun getExplorePictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getExploreNodeReference())
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getRecentPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getTopPicksNodeReference()
        .child(CHILD_PATH_RECENT))
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getPopularPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getTopPicksNodeReference()
        .child(CHILD_PATH_POPULAR))
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getStandoutPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getTopPicksNodeReference()
        .child(CHILD_PATH_STANDOUT))
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getBuildingsPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(CHILD_PATH_BUILDING))
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getFoodPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(CHILD_PATH_FOOD))
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getNaturePictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(CHILD_PATH_NATURE))
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getObjectsPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(CHILD_PATH_OBJECT))
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getPeoplePictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(CHILD_PATH_PEOPLE))
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getTechnologyPictures(): Single<List<ImageModel>> {
    return getPicturesFromFirebase(getCategoriesNodeReference()
        .child(CHILD_PATH_TECHNOLOGY))
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getImageBitmap(link: String): Observable<ImageDownloadModel> {
    if (!fileHandler.freeSpaceAvailable()) {
      return Observable.error(NotEnoughFreeSpaceException())
    }
    if (imageHandler.isImageCached(link)) {
      val observable: Observable<ImageDownloadModel> = Observable.create {
        it.onNext(ImageDownloadModel(IMAGE_DOWNLOAD_PROGRESS_VALUE_99, null))
        it.onNext(ImageDownloadModel(IMAGE_DOWNLOAD_FINISHED_VALUE, imageHandler.getImageBitmap()))
        it.onComplete()
      }
      return observable.subscribeOn(executionThread.ioScheduler)
    }
    return imageHandler.fetchImage(link)
        .subscribeOn(executionThread.ioScheduler)
        .flatMap {
          if (it == IMAGE_DOWNLOAD_FINISHED_VALUE) {
            Observable.just(ImageDownloadModel(it, imageHandler.getImageBitmap()))
          } else {
            Observable.just(ImageDownloadModel(it, null))
          }
        }
  }

  override fun getCacheImageBitmap(): Single<Bitmap> {
    val single: Single<Bitmap> = Single.create { it.onSuccess(imageHandler.getImageBitmap()) }
    return single.subscribeOn(executionThread.computationScheduler)
  }

  override fun getShortImageLink(link: String): Single<String> {
    val single: Single<String> = Single.create {
      it.onSuccess(urlShortener.shortUrl(link))
    }
    return single.subscribeOn(executionThread.ioScheduler)
  }

  override fun clearImageCaches(): Completable {
    return imageHandler.clearImageCache()
        .subscribeOn(executionThread.computationScheduler)
  }

  override fun cancelImageBitmapFetchOperation() {
    imageHandler.cancelFetchingImage()
  }

  override fun getCacheSourceUri() = imageHandler.getImageUri()

  override fun getCacheResultUri() = fileHandler.getCacheFileUriForCropping()

  override fun getBitmapFromUri(uri: Uri): Single<Bitmap> {
    return imageHandler.convertUriToBitmap(uri)
        .subscribeOn(executionThread.computationScheduler)
  }

  override fun downloadImage(link: String): Completable {
    return downloadHelper.downloadImage(link)
        .subscribeOn(executionThread.ioScheduler)

  }

  override fun crystallizeImage(): Single<Pair<Boolean, Bitmap>> {
    return imageHandler.convertImageInCacheToLowpoly()
        .subscribeOn(executionThread.computationScheduler)
        .map {
          Pair(true, it)
        }
  }

  override fun saveCrystallizedImageToDownloads(): Completable {
    return imageHandler.saveLowPolyImageToDownloads()
        .subscribeOn(executionThread.computationScheduler)
  }

  override fun isCrystallizeDescriptionShown(): Boolean {
    return sharedPrefsHelper.getBoolean(IMAGE_PREFERENCE_NAME,
        CRYSTALLIZE_HINT_DIALOG_SHOWN_BEFORE_TAG)
  }

  override fun rememberCrystallizeDescriptionShown() {
    sharedPrefsHelper.setBoolean(IMAGE_PREFERENCE_NAME, CRYSTALLIZE_HINT_DIALOG_SHOWN_BEFORE_TAG,
        true)
  }

  override fun checkIfDownloadIsInProgress(link: String): Boolean {
    return downloadHelper.isDownloadEnqueued(link)
  }

  override fun saveImageToCollections(type: Int, details: String): Completable {
    return imageHandler.saveImageToCollections(type, details)
        .subscribeOn(executionThread.computationScheduler)
  }

  override fun isCustomMinimalColorListPresent(): Boolean {
    return sharedPrefsHelper.getBoolean(IMAGE_PREFERENCE_NAME,
        CUSTOM_MINIMAL_COLOR_LIST_AVAILABLE_TAG, false)
  }

  override fun getCustomMinimalColorList(): Single<List<String>> {
    return minimalColorHelper.getCustomColors()
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun getDefaultMinimalColorList(): Single<List<String>> {
    return minimalColorHelper.getDefaultColors()
        .subscribeOn(executionThread.ioScheduler)
  }

  override fun saveCustomMinimalColorList(colors: List<String>): Completable {
    return Completable.create {
      if (
          sharedPrefsHelper.setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
              gsonDataHelper.getString(colors))
      ) {
        it.onComplete()
      } else {
        it.onError(Exception())
      }
    }.subscribeOn(executionThread.ioScheduler)
  }

  override fun modifyColorList(
    colors: List<String>,
    selectedIndicesMap: HashMap<Int, String>
  ): Single<List<String>> {
    return modifyAndSaveList(colors.toMutableList(), selectedIndicesMap)
        .subscribeOn(executionThread.computationScheduler)
  }

  override fun restoreDeletedColors(): Single<RestoreColorsModel> {
    val list = mutableListOf<String>()
    return minimalColorHelper.getCustomColors()
        .flatMap {
          list.clear()
          list.addAll(it)
          minimalColorHelper.getDeletedItemsFromCache()
        }.flatMap { map ->
          if (map.isEmpty()) {
            Single.error(EmptyRecentlyDeletedMapException())
          } else {
            map.keys.forEach {
              list.add(it, map[it]!!)
            }
            sharedPrefsHelper.setString(IMAGE_PREFERENCE_NAME,
                CUSTOM_MINIMAL_COLOR_LIST_TAG,
                gsonDataHelper.getString(list))
            Single.just(RestoreColorsModel(list, map))
          }
        }
        .subscribeOn(executionThread.computationScheduler)
  }

  internal fun getExploreNodeReference() = firebaseDatabaseHelper.getDatabase()
      .getReference(FIREBASE_DATABASE_PATH)
      .child(CHILD_PATH_EXPLORE)

  private fun getTopPicksNodeReference() = firebaseDatabaseHelper.getDatabase()
      .getReference(FIREBASE_DATABASE_PATH)
      .child(CHILD_PATH_TOP_PICKS)

  private fun getCategoriesNodeReference() = firebaseDatabaseHelper.getDatabase()
      .getReference(FIREBASE_DATABASE_PATH)
      .child(CHILD_PATH_CATEGORIES)

  private fun getPicturesFromFirebase(firebaseDatabaseReference: DatabaseReference): Single<List<ImageModel>> {
    val imageList = mutableListOf<FirebaseImageEntity>()
    return firebaseDatabaseHelper
        .fetch(firebaseDatabaseReference)
        .flatMap {
          it.values.forEach { jsonString ->
            imageList.add(gsonDataHelper.getImageEntity(jsonString))
          }
          imageList.reverse()
          val image = firebasePictureEntityMapper.mapFromEntity(imageList)
          Single.just(image)
        }
        .timeout(FIREBASE_TIMEOUT_DURATION.toLong(), SECONDS)
  }

  private fun modifyAndSaveList(
    colors: MutableList<String>,
    selectedIndicesMap: HashMap<Int, String>
  ): Single<List<String>> {
    return minimalColorHelper.cacheDeletedItems(selectedIndicesMap)
        .andThen(removeElementsFromList(colors, selectedIndicesMap))
        .andThen(saveModifiedColors(colors))
  }

  private fun removeElementsFromList(
    colors: MutableList<String>,
    selectedIndicesMap: HashMap<Int, String>
  ): Completable {
    return Completable.create { emitter ->
      TreeMap<Int, String>(Collections.reverseOrder()).let {
        it.putAll(selectedIndicesMap)
        it.keys.forEach { position ->
          colors.removeAt(position)
        }
        emitter.onComplete()
      }
    }
  }

  private fun saveModifiedColors(
    colors: List<String>
  ): Single<List<String>> {
    return Single.create {
      if (
          sharedPrefsHelper.setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
              gsonDataHelper.getString(colors))) {
        sharedPrefsHelper.setBoolean(IMAGE_PREFERENCE_NAME,
            CUSTOM_MINIMAL_COLOR_LIST_AVAILABLE_TAG,
            true)
        it.onSuccess(colors)
      } else {
        it.onError(Exception())
      }
    }
  }

}