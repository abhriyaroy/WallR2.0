package zebrostudio.wallr100.data

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.pddstudio.urlshortener.URLShortener
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.utils.GsonProvider
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.database.DatabaseImageType.CRYSTALLIZED
import zebrostudio.wallr100.data.database.DatabaseImageType.EDITED
import zebrostudio.wallr100.data.database.DatabaseImageType.MINIMAL_COLOR
import zebrostudio.wallr100.data.database.DatabaseImageType.SEARCH
import zebrostudio.wallr100.data.database.DatabaseImageType.WALLPAPER
import zebrostudio.wallr100.data.datafactory.FirebaseImageEntityModelFactory
import zebrostudio.wallr100.data.datafactory.UnsplashPictureEntityModelFactory
import zebrostudio.wallr100.data.exception.EmptyRecentlyDeletedMapException
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.NotEnoughFreeSpaceException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapper
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapper
import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity
import zebrostudio.wallr100.domain.executor.ExecutionThread
import zebrostudio.wallr100.domain.model.CollectionsImageModel
import zebrostudio.wallr100.domain.model.RestoreColorsModel
import java.util.TreeMap
import java.util.UUID.randomUUID
import java.util.concurrent.TimeUnit

@RunWith(MockitoJUnitRunner::class)
class WallrDataRepositoryTest {

  @Mock lateinit var executionThread: ExecutionThread
  @Mock lateinit var sharedPrefs: SharedPrefsHelper
  @Mock lateinit var remoteAuthServiceFactory: RemoteAuthServiceFactory
  @Mock lateinit var unsplashClientFactory: UnsplashClientFactory
  @Mock lateinit var firebaseDatabaseHelper: FirebaseDatabaseHelper
  @Mock lateinit var databaseReference: DatabaseReference
  @Mock lateinit var firebaseDatabase: FirebaseDatabase
  @Mock lateinit var urlShortener: URLShortener
  @Mock lateinit var imageHandler: ImageHandler
  @Mock lateinit var fileHandler: FileHandler
  @Mock lateinit var downloadHelper: DownloadHelper
  @Mock lateinit var minimalColorHelper: MinimalColorHelper
  @Mock lateinit var mockBitmap: Bitmap
  @Mock lateinit var mockUri: Uri
  @Mock lateinit var gsonProvider: GsonProvider
  private lateinit var unsplashPictureEntityMapper: UnsplashPictureEntityMapper
  private lateinit var firebasePictureEntityMapper: FirebasePictureEntityMapper
  private lateinit var wallrDataRepository: WallrDataRepository
  private val randomString = randomUUID().toString()
  private val dummyInt = 500 // to force some error other than 403 or 404
  private val firstElementIndex = 0

  @Before
  fun setup() {
    unsplashPictureEntityMapper = UnsplashPictureEntityMapper()
    firebasePictureEntityMapper = FirebasePictureEntityMapper()
    wallrDataRepository =
        WallrDataRepository(remoteAuthServiceFactory, unsplashClientFactory, sharedPrefs,
            gsonProvider, unsplashPictureEntityMapper, firebaseDatabaseHelper,
            firebasePictureEntityMapper, urlShortener, imageHandler, fileHandler, downloadHelper,
            minimalColorHelper, executionThread)

    `when`(executionThread.ioScheduler).thenReturn(Schedulers.trampoline())
    `when`(executionThread.computationScheduler).thenReturn(Schedulers.trampoline())
  }

  @Test fun `should return single on server success response`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("success", dummyInt, randomString)))

    wallrDataRepository.authenticatePurchase(randomString, randomString, randomString)
        .test()
        .assertComplete()

    `should verify io scheduler call`()
  }

  @Test
  fun `should return invalid purchase exception on authenticatePurchase 403 error response`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("error", 403, randomString)))

    wallrDataRepository.authenticatePurchase(randomString, randomString, randomString)
        .test()
        .assertError(InvalidPurchaseException::class.java)

    `should verify io scheduler call`()
  }

  @Test
  fun `should return invalid purchase exception on authenticatePurchase 404 error response`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("error", 404, randomString)))

    wallrDataRepository.authenticatePurchase(randomString, randomString, randomString)
        .test()
        .assertError(InvalidPurchaseException::class.java)

    verify(remoteAuthServiceFactory).verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString))
    verifyNoMoreInteractions(remoteAuthServiceFactory)
    `should verify io scheduler call`()
  }

  @Test
  fun `should return unable to verify purchase exception on some error during authenticatePurchase call`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity(randomString, dummyInt, randomString)))

    wallrDataRepository.authenticatePurchase(randomString, randomString, randomString)
        .test()
        .assertError(UnableToVerifyPurchaseException::class.java)

    verify(remoteAuthServiceFactory).verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString))
    verifyNoMoreInteractions(remoteAuthServiceFactory)
    `should verify io scheduler call`()
  }

  @Test fun `should return true after successfully updating purchase status`() {
    `when`(sharedPrefs.setBoolean(PURCHASE_PREFERENCE_NAME,
        PREMIUM_USER_TAG, true)).thenReturn(true)

    assertEquals(true, wallrDataRepository.updateUserPurchaseStatus())
  }

  @Test fun `should return false after unsuccessful update of purchase status`() {
    `when`(sharedPrefs.setBoolean(PURCHASE_PREFERENCE_NAME,
        PREMIUM_USER_TAG, true)).thenReturn(false)

    assertEquals(false, wallrDataRepository.updateUserPurchaseStatus())

    verify(sharedPrefs).setBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, true)
    verifyNoMoreInteractions(sharedPrefs)
  }

  @Test fun `should return true after checking if user is a premium user`() {
    `when`(sharedPrefs.getBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG,
        false)).thenReturn(true)

    assertEquals(true, wallrDataRepository.isUserPremium())

    verify(sharedPrefs).getBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, false)
    verifyNoMoreInteractions(sharedPrefs)
  }

  @Test fun `should return false after checking if user is a premium user`() {
    `when`(sharedPrefs.getBoolean(PURCHASE_PREFERENCE_NAME,
        PREMIUM_USER_TAG, false)).thenReturn(false)

    assertEquals(false, wallrDataRepository.isUserPremium())

    verify(sharedPrefs).getBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, false)
    verifyNoMoreInteractions(sharedPrefs)
  }

  @Test fun `should return no result found exception on getPictures call success`() {
    `when`(unsplashClientFactory.getPicturesService(randomString)).thenReturn(
        Single.just(emptyList()))

    wallrDataRepository.getSearchPictures(randomString)
        .test()
        .assertError(NoResultFoundException::class.java)

    verify(unsplashClientFactory).getPicturesService(randomString)
    verifyNoMoreInteractions(unsplashClientFactory)
    `should verify io scheduler call`()
  }

  @Test fun `should return unable to resolve host exception on getPictures call failure`() {
    `when`(unsplashClientFactory.getPicturesService(randomString)).thenReturn(
        Single.error(Exception(UNABLE_TO_RESOLVE_HOST_EXCEPTION_MESSAGE)))

    wallrDataRepository.getSearchPictures(randomString)
        .test()
        .assertError(UnableToResolveHostException::class.java)

    verify(unsplashClientFactory).getPicturesService(randomString)
    verifyNoMoreInteractions(unsplashClientFactory)
    `should verify io scheduler call`()
  }

  @Test fun `should return mapped search pictures model list on getPictures call failure`() {
    val unsplashPicturesEntityList = mutableListOf(
        UnsplashPictureEntityModelFactory.getUnsplashPictureEntityModel())

    val searchPicturesModelList = unsplashPictureEntityMapper
        .mapFromEntity(unsplashPicturesEntityList)

    `when`(unsplashClientFactory.getPicturesService(randomString)).thenReturn(
        Single.just(unsplashPicturesEntityList))

    val searchPicturesResult = wallrDataRepository.getSearchPictures(randomString)
        .test()
        .values()[0][0]

    assertTrue(searchPicturesModelList[0] == searchPicturesResult)
    verify(unsplashClientFactory).getPicturesService(randomString)
    verifyNoMoreInteractions(unsplashClientFactory)
    `should verify io scheduler call`()
  }

  @Test fun `should return explore node reference on getNodeReference call`() {
    stubFirebaseDatabaseNode(CHILD_PATH_EXPLORE)

    val nodeReference = wallrDataRepository.getExploreNodeReference()

    assertTrue(nodeReference == databaseReference)
    verify(firebaseDatabaseHelper, times(3)).getDatabase()
    verifyNoMoreInteractions(firebaseDatabaseHelper)
  }

  @Test fun `should return shortened image link on getShortImageLink call success`() {
    `when`(urlShortener.shortUrl(randomString)).thenReturn(randomString)

    wallrDataRepository.getShortImageLink(randomString).test().assertValue(randomString)

    verify(urlShortener).shortUrl(randomString)
    verifyNoMoreInteractions(urlShortener)
  }

  @Test fun `should return NotEnoughFreeSpace exception on getImageBitmap call error`() {
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(false)

    wallrDataRepository.getImageBitmap(randomString).test()
        .assertError(NotEnoughFreeSpaceException::class.java)

    verify(fileHandler).freeSpaceAvailable()
    verifyNoMoreInteractions(fileHandler)
  }

  @Test fun `should return cached bitmap on getImageBitmap call success and cache is present`() {
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(true)
    `when`(imageHandler.isImageCached(randomString)).thenReturn(true)
    `when`(imageHandler.getImageBitmap()).thenReturn(mockBitmap)
    val testObserver = wallrDataRepository.getImageBitmap(randomString).test()
    val resultImageDownloadModel = testObserver.values()[0]
    val resultImageDownloadModelCompleted = testObserver.values()[1]

    assertEquals(resultImageDownloadModel.progress, IMAGE_DOWNLOAD_PROGRESS_VALUE_99)
    assertEquals(resultImageDownloadModel.imageBitmap, null)
    assertEquals(resultImageDownloadModelCompleted.progress, DOWNLOAD_PROGRESS_COMPLETED_VALUE)
    assertEquals(resultImageDownloadModelCompleted.imageBitmap, mockBitmap)

    verify(fileHandler).freeSpaceAvailable()
    verifyNoMoreInteractions(fileHandler)
    verify(imageHandler).isImageCached(randomString)
    verify(imageHandler).getImageBitmap()
    verifyNoMoreInteractions(imageHandler)
    `should verify io scheduler call`()
  }

  @Test
  fun `should return imageDownloadModel with only progress on getImageBitmap call success and download in progress`() {
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(true)
    `when`(imageHandler.isImageCached(randomString)).thenReturn(false)
    `when`(imageHandler.fetchImage(randomString)).thenReturn(
        Observable.just(IMAGE_DOWNLOAD_PROGRESS_VALUE_99))

    val resultImageDownloadModel =
        wallrDataRepository.getImageBitmap(randomString).test().values()[0]

    assertEquals(resultImageDownloadModel.progress, IMAGE_DOWNLOAD_PROGRESS_VALUE_99)
    assertEquals(resultImageDownloadModel.imageBitmap, null)
    verify(fileHandler).freeSpaceAvailable()
    verifyNoMoreInteractions(fileHandler)
    verify(imageHandler).fetchImage(randomString)
    verify(imageHandler).isImageCached(randomString)
    verifyNoMoreInteractions(imageHandler)
    `should verify io scheduler call`()
  }

  @Test
  fun `should return imageDownloadProgress with progress and bitmap on getImageBitmap call success and download is completed`() {
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(true)
    `when`(imageHandler.isImageCached(randomString)).thenReturn(false)
    `when`(imageHandler.fetchImage(randomString)).thenReturn(
        Observable.just(DOWNLOAD_PROGRESS_COMPLETED_VALUE))
    `when`(imageHandler.getImageBitmap()).thenReturn(mockBitmap)

    val resultImageDownloadModel =
        wallrDataRepository.getImageBitmap(randomString).test().values()[0]

    assertEquals(resultImageDownloadModel.progress, DOWNLOAD_PROGRESS_COMPLETED_VALUE)
    assertEquals(resultImageDownloadModel.imageBitmap, mockBitmap)
    verify(fileHandler).freeSpaceAvailable()
    verifyNoMoreInteractions(fileHandler)
    verify(imageHandler).fetchImage(randomString)
    verify(imageHandler).isImageCached(randomString)
    verify(imageHandler).getImageBitmap()
    verifyNoMoreInteractions(imageHandler)
    `should verify io scheduler call`()
  }

  @Test fun `should return Single of bitmap on getCacheImageBitmap call success`() {
    `when`(imageHandler.getImageBitmap()).thenReturn(mockBitmap)

    wallrDataRepository.getCacheImageBitmap().test().assertValue(mockBitmap)

    verify(imageHandler).getImageBitmap()
    verifyNoMoreInteractions(imageHandler)
    `should verify computation scheduler call`()
  }

  @Test fun `should complete on clearImageCaches call success`() {
    `when`(imageHandler.clearImageCache()).thenReturn(Completable.complete())

    wallrDataRepository.clearImageCaches().test().assertComplete()

    `should verify computation scheduler call`()
  }

  @Test fun `should invoke cancelImageFetching on cancelImageBitmapFetchOperation call success`() {
    wallrDataRepository.cancelImageBitmapFetchOperation()

    verify(imageHandler).cancelFetchingImage()
    verifyNoMoreInteractions(imageHandler)
  }

  @Test fun `should return image uri on getCacheSourceUri call success`() {
    `when`(imageHandler.getImageUri()).thenReturn(mockUri)

    val uri = wallrDataRepository.getCacheSourceUri()

    assertEquals(mockUri, uri)
    verify(imageHandler).getImageUri()
    verifyNoMoreInteractions(imageHandler)
  }

  @Test fun `should return result destination file uri on getCacheResultUri call success`() {
    `when`(fileHandler.getCacheFileUriForCropping()).thenReturn(mockUri)

    val uri = wallrDataRepository.getCacheResultUri()

    assertEquals(mockUri, uri)
    verify(fileHandler).getCacheFileUriForCropping()
    verifyNoMoreInteractions(fileHandler)
  }

  @Test fun `should return Single of bitmap on getBitmapFromUri call success`() {
    `when`(imageHandler.convertUriToBitmap(mockUri)).thenReturn(Single.just(mockBitmap))

    wallrDataRepository.getBitmapFromUri(mockUri).test()
        .assertValue(mockBitmap)

    verify(imageHandler).convertUriToBitmap(mockUri)
    verifyNoMoreInteractions(imageHandler)
    `should verify computation scheduler call`()
  }

  @Test fun `should complete successfully on downloadImage call success`() {
    `when`(downloadHelper.downloadImage(randomString)).thenReturn(Completable.complete())

    wallrDataRepository.downloadImage(randomString).test().assertComplete()

    verify(downloadHelper).downloadImage(randomString)
    verifyNoMoreInteractions(downloadHelper)
    `should verify io scheduler call`()
  }

  @Test fun `should return true on checkIfDownloadIsInProgress call success`() {
    `when`(downloadHelper.isDownloadEnqueued(randomString)).thenReturn(true)

    assertTrue(wallrDataRepository.checkIfDownloadIsInProgress(randomString))

    verify(downloadHelper).isDownloadEnqueued(randomString)
    verifyNoMoreInteractions(downloadHelper)
  }

  @Test fun `should return pair on crystallize image call success`() {
    `when`(imageHandler.convertImageInCacheToLowpoly()).thenReturn(Single.just(mockBitmap))

    val result = wallrDataRepository.crystallizeImage().test().values()[0]

    assertEquals(true, result.first)
    assertEquals(mockBitmap, result.second)
    verify(imageHandler).convertImageInCacheToLowpoly()
    verifyNoMoreInteractions(imageHandler)
    `should verify computation scheduler call`()
  }

  @Test fun `should complete on saveCrystallizedImageToDownloads call success`() {
    `when`(imageHandler.saveCacheImageToDownloads()).thenReturn(Completable.complete())

    wallrDataRepository.saveCachedImageToDownloads().test().assertComplete()

    verify(imageHandler).saveCacheImageToDownloads()
    verifyNoMoreInteractions(imageHandler)
    `should verify computation scheduler call`()
  }

  @Test fun `should return false on isCrystallizeDescriptionShown call success`() {
    `when`(sharedPrefs.getBoolean(IMAGE_PREFERENCE_NAME, CRYSTALLIZE_HINT_DIALOG_SHOWN_BEFORE_TAG))
        .thenReturn(false)

    assertFalse(wallrDataRepository.isCrystallizeDescriptionShown())

    verify(sharedPrefs).getBoolean(IMAGE_PREFERENCE_NAME, CRYSTALLIZE_HINT_DIALOG_SHOWN_BEFORE_TAG)
    verifyNoMoreInteractions(sharedPrefs)
  }

  @Test fun `should call shared preference on rememberCrystallizeDescriptionShown call success`() {
    wallrDataRepository.rememberCrystallizeDescriptionShown()

    verify(sharedPrefs).setBoolean(IMAGE_PREFERENCE_NAME, CRYSTALLIZE_HINT_DIALOG_SHOWN_BEFORE_TAG,
        true)
    verifyNoMoreInteractions(sharedPrefs)
  }

  @Test fun `should complete on saveImageToCollections call success of type wallpaper`() {
    `when`(
        imageHandler.saveImageToCollections(randomString, WALLPAPER)).thenReturn(
        Completable.complete())

    wallrDataRepository.saveImageToCollections(randomString, CollectionsImageModel.WALLPAPER).test()
        .assertComplete()

    verify(imageHandler).saveImageToCollections(randomString, WALLPAPER)
    verifyNoMoreInteractions(imageHandler)
    `should verify computation scheduler call`()
  }

  @Test fun `should complete on saveImageToCollections call success of type search`() {
    `when`(
        imageHandler.saveImageToCollections(randomString, SEARCH)).thenReturn(
        Completable.complete())

    wallrDataRepository.saveImageToCollections(randomString, CollectionsImageModel.SEARCH).test()
        .assertComplete()

    verify(imageHandler).saveImageToCollections(randomString, SEARCH)
    verifyNoMoreInteractions(imageHandler)
    `should verify computation scheduler call`()
  }

  @Test fun `should complete on saveImageToCollections call success of type edited`() {
    `when`(
        imageHandler.saveImageToCollections(randomString, EDITED)).thenReturn(
        Completable.complete())

    wallrDataRepository.saveImageToCollections(randomString, CollectionsImageModel.EDITED).test()
        .assertComplete()

    verify(imageHandler).saveImageToCollections(randomString, EDITED)
    verifyNoMoreInteractions(imageHandler)
    `should verify computation scheduler call`()
  }

  @Test fun `should complete on saveImageToCollections call success of type crystallized`() {
    `when`(
        imageHandler.saveImageToCollections(randomString, CRYSTALLIZED)).thenReturn(
        Completable.complete())

    wallrDataRepository.saveImageToCollections(randomString, CollectionsImageModel.CRYSTALLIZED)
        .test()
        .assertComplete()

    verify(imageHandler).saveImageToCollections(randomString, CRYSTALLIZED)
    verifyNoMoreInteractions(imageHandler)
    `should verify computation scheduler call`()
  }

  @Test fun `should complete on saveImageToCollections call success of type minimal color`() {
    `when`(
        imageHandler.saveImageToCollections(randomString, MINIMAL_COLOR)).thenReturn(
        Completable.complete())

    wallrDataRepository.saveImageToCollections(randomString, CollectionsImageModel.MINIMAL_COLOR)
        .test()
        .assertComplete()

    verify(imageHandler).saveImageToCollections(randomString, MINIMAL_COLOR)
    verifyNoMoreInteractions(imageHandler)
    `should verify computation scheduler call`()
  }

  @Test fun `should return Single of ImageModel list on getExplorePictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(databaseReference)
    `when`(databaseReference.child(
        CHILD_PATH_EXPLORE)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getExplorePictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    `should verify firebase database helper calls to get image model`()
    `should verify io scheduler call`()
  }

  @Test fun `should return Single of ImageModel list on getRecentPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_TOP_PICKS)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_RECENT)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getRecentPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    `should verify firebase database helper calls to get image model`()
    `should verify io scheduler call`()
  }

  @Test fun `should return Single of ImageModel list on getPopularPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_TOP_PICKS)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_POPULAR)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getPopularPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    `should verify firebase database helper calls to get image model`()
    `should verify io scheduler call`()
  }

  @Test fun `should return Single of ImageModel list on getStandoutPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_TOP_PICKS)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_STANDOUT)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getStandoutPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    `should verify firebase database helper calls to get image model`()
    `should verify io scheduler call`()
  }

  @Test fun `should return Single of ImageModel list on getBuildingsPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_CATEGORIES)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_BUILDING)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getBuildingsPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    `should verify firebase database helper calls to get image model`()
    `should verify io scheduler call`()
  }

  @Test fun `should return Single of ImageModel list on getFoodPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_CATEGORIES)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_FOOD)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getFoodPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    `should verify firebase database helper calls to get image model`()
    `should verify io scheduler call`()
  }

  @Test fun `should return Single of ImageModel list on getNaturePictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_CATEGORIES)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_NATURE)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getNaturePictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    `should verify firebase database helper calls to get image model`()
    `should verify io scheduler call`()
  }

  @Test fun `should return Single of ImageModel list on getObjectsPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_CATEGORIES)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_OBJECT)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getObjectsPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    `should verify firebase database helper calls to get image model`()
    `should verify io scheduler call`()
  }

  @Test fun `should return Single of ImageModel list on getPeoplePictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_CATEGORIES)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_PEOPLE)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getPeoplePictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    `should verify firebase database helper calls to get image model`()
    `should verify io scheduler call`()
  }

  @Test fun `should return Single of ImageModel list on getTechnologyPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_CATEGORIES)).thenReturn(databaseReference)
    `when`(databaseReference.child(CHILD_PATH_TECHNOLOGY)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getTechnologyPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    `should verify firebase database helper calls to get image model`()
    `should verify io scheduler call`()
  }

  @Test fun `should return true on isCustomColorListPresent call success`() {
    `when`(sharedPrefs.getBoolean(IMAGE_PREFERENCE_NAME,
        CUSTOM_MINIMAL_COLOR_LIST_AVAILABLE_TAG)).thenReturn(true)

    assertTrue(wallrDataRepository.isCustomMinimalColorListPresent())

    verify(sharedPrefs).getBoolean(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_AVAILABLE_TAG)
    verifyNoMoreInteractions(sharedPrefs)
  }

  @Test fun `should return single of list of string on getCustomColorList call success`() {
    val list = listOf(randomString)
    `when`(minimalColorHelper.getCustomColors()).thenReturn(Single.just(list))

    wallrDataRepository.getCustomMinimalColorList().test().assertValue(list)

    verify(minimalColorHelper).getCustomColors()
    verifyNoMoreInteractions(minimalColorHelper)
    `should verify io scheduler call`()
  }

  @Test fun `should return single of list of string on getDefaultColorList call success`() {
    val list = listOf(randomString)
    `when`(minimalColorHelper.getDefaultColors()).thenReturn(Single.just(list))

    wallrDataRepository.getDefaultMinimalColorList().test().assertValue(list)

    verify(minimalColorHelper).getDefaultColors()
    verifyNoMoreInteractions(minimalColorHelper)
    `should verify io scheduler call`()
  }

  @Test fun `should complete saveCustomMinimalColorList call success`() {
    val list = listOf(randomString)
    val gsonString = Gson().toJson(list)
    `when`(gsonProvider.getGson()).thenReturn(Gson())
    `when`(sharedPrefs.setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
        gsonProvider.getGson().toJson(list))).thenReturn(true)

    wallrDataRepository.saveCustomMinimalColorList(list).test().assertComplete()

    verify(sharedPrefs).setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
        gsonString)
    verifyNoMoreInteractions(sharedPrefs)
    `should verify io scheduler call`()
  }

  @Test fun `should return single of list of strings on modifyColorList call success`() {
    val list = listOf(randomString, randomString)
    val modifiedList = listOf(randomString)
    val gsonString = Gson().toJson(modifiedList)
    val selectedIndices = hashMapOf(Pair(firstElementIndex, randomString))
    `when`(gsonProvider.getGson()).thenReturn(Gson())
    `when`(minimalColorHelper.cacheDeletedItems(selectedIndices)).thenReturn(Completable.complete())
    `when`(sharedPrefs.setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
        gsonString)).thenReturn(true)

    wallrDataRepository.modifyColorList(list, selectedIndices).test().assertValue(modifiedList)

    verify(minimalColorHelper).cacheDeletedItems(selectedIndices)
    verifyNoMoreInteractions(minimalColorHelper)
    verify(sharedPrefs).setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
        gsonString)
    verify(sharedPrefs).setBoolean(IMAGE_PREFERENCE_NAME,
        CUSTOM_MINIMAL_COLOR_LIST_AVAILABLE_TAG, true)
    verifyNoMoreInteractions(sharedPrefs)
    `should verify computation scheduler call`()
  }

  @Test fun `should return error on modifyColorList call failure`() {
    val list = listOf(randomString, randomString)
    val modifiedList = listOf(randomString)
    val gsonString = Gson().toJson(modifiedList)
    val selectedIndices = hashMapOf(Pair(firstElementIndex, randomString))
    `when`(gsonProvider.getGson()).thenReturn(Gson())
    `when`(minimalColorHelper.cacheDeletedItems(selectedIndices)).thenReturn(Completable.complete())
    `when`(sharedPrefs.setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
        gsonString)).thenReturn(false)

    wallrDataRepository.modifyColorList(list, selectedIndices).test()
        .assertError(Exception::class.java)

    verify(minimalColorHelper).cacheDeletedItems(selectedIndices)
    verifyNoMoreInteractions(minimalColorHelper)
    verify(sharedPrefs).setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
        gsonString)
    verifyNoMoreInteractions(sharedPrefs)
    `should verify computation scheduler call`()
  }

  @Test
  fun `should return single of list of restore colors model on restoreDeletedColors call success`() {
    val list = listOf(randomString)
    val modifiedList = listOf(randomString, randomString)
    val selectedIndices = TreeMap<Int, String>(mapOf(Pair(firstElementIndex, randomString)))
    val restoreColorsModel = RestoreColorsModel(modifiedList, selectedIndices)
    `when`(gsonProvider.getGson()).thenReturn(Gson())
    `when`(minimalColorHelper.getCustomColors()).thenReturn(Single.just(list))
    `when`(minimalColorHelper.getDeletedItemsFromCache()).thenReturn(Single.just(selectedIndices))

    wallrDataRepository.restoreDeletedColors().test().assertValue(restoreColorsModel)

    verify(minimalColorHelper).getCustomColors()
    verify(minimalColorHelper).getDeletedItemsFromCache()
    verifyNoMoreInteractions(minimalColorHelper)
    `should verify computation scheduler call`()
  }

  @Test
  fun `should return EmptyRecentlyDeletedMapException on restoreDeletedColors call failure due to empty selected map `() {
    val list = listOf(randomString)
    val selectedIndices = TreeMap<Int, String>()
    `when`(minimalColorHelper.getCustomColors()).thenReturn(Single.just(list))
    `when`(minimalColorHelper.getDeletedItemsFromCache()).thenReturn(Single.just(selectedIndices))

    wallrDataRepository.restoreDeletedColors().test()
        .assertError(EmptyRecentlyDeletedMapException::class.java)

    verify(minimalColorHelper).getCustomColors()
    verify(minimalColorHelper).getDeletedItemsFromCache()
    verifyNoMoreInteractions(minimalColorHelper)
    `should verify computation scheduler call`()
  }

  private fun stubFirebaseDatabaseNode(childPath: String) {
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabaseHelper.getDatabase().getReference(FIREBASE_DATABASE_PATH)).thenReturn(
        databaseReference)
    `when`(firebaseDatabaseHelper.getDatabase().getReference(FIREBASE_DATABASE_PATH)
        .child(childPath)).thenReturn(databaseReference)
  }

  private fun `should verify io scheduler call`() {
    verify(executionThread).ioScheduler
    verifyNoMoreInteractions(executionThread)
  }

  private fun `should verify computation scheduler call`() {
    verify(executionThread).computationScheduler
    verifyNoMoreInteractions(executionThread)
  }

  private fun `should verify firebase database helper calls to get image model`() {
    verify(firebaseDatabaseHelper).getDatabase()
    verify(firebaseDatabaseHelper).fetch(databaseReference)
    verifyNoMoreInteractions(firebaseDatabaseHelper)
  }

}
