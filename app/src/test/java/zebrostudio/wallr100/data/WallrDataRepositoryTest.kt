package zebrostudio.wallr100.data

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.After
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
import zebrostudio.wallr100.data.datafactory.CollectionsDatabaseImageEntityModelFactory.getCollectionsDatabaseImageEntity
import zebrostudio.wallr100.data.datafactory.FirebaseImageEntityModelFactory.getFirebaseImageEntity
import zebrostudio.wallr100.data.datafactory.UnsplashPictureEntityModelFactory.getUnsplashPictureEntityModel
import zebrostudio.wallr100.data.exception.AlreadyPresentInCollectionException
import zebrostudio.wallr100.data.exception.EmptyRecentlyDeletedMapException
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.NotEnoughFreeSpaceException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.data.mapper.CollectionsDatabaseImageEntityMapper
import zebrostudio.wallr100.data.mapper.DatabaseImageTypeMapper
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapper
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapper
import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity
import zebrostudio.wallr100.data.urlshortener.UrlShortener
import zebrostudio.wallr100.domain.datafactory.CollectionsImageModelFactory.getCollectionsImageModel
import zebrostudio.wallr100.domain.datafactory.ImageModelFactory.getImageModel
import zebrostudio.wallr100.domain.datafactory.SearchPicturesModelFactory.getSearchPicturesModel
import zebrostudio.wallr100.domain.executor.ExecutionThread
import zebrostudio.wallr100.domain.model.RestoreColorsModel
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.GRADIENT
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.MATERIAL
import zebrostudio.wallr100.presentation.minimal.MultiColorImageType.PLASMA
import java.util.Random
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
  @Mock lateinit var urlShortener: UrlShortener
  @Mock lateinit var imageHandler: ImageHandler
  @Mock lateinit var fileHandler: FileHandler
  @Mock lateinit var downloadHelper: DownloadHelper
  @Mock lateinit var minimalColorHelper: MinimalColorHelper
  @Mock lateinit var mockBitmap: Bitmap
  @Mock lateinit var mockUri: Uri
  @Mock lateinit var gsonProvider: GsonProvider
  @Mock lateinit var collectionsDatabaseImageEntityMapper: CollectionsDatabaseImageEntityMapper
  @Mock lateinit var databaseImageTypeMapper: DatabaseImageTypeMapper
  @Mock lateinit var unsplashPictureEntityMapper: UnsplashPictureEntityMapper
  @Mock lateinit var firebasePictureEntityMapper: FirebasePictureEntityMapper

  private lateinit var wallrDataRepository: WallrDataRepository
  private val randomString = randomUUID().toString()
  private val dummyInt = 500 // to force some error other than 403 or 404
  private val firstElementIndex = 0

  @Before
  fun setup() {
    wallrDataRepository =
        WallrDataRepository(remoteAuthServiceFactory, unsplashClientFactory, sharedPrefs,
            gsonProvider, collectionsDatabaseImageEntityMapper, databaseImageTypeMapper,
            unsplashPictureEntityMapper, firebaseDatabaseHelper, firebasePictureEntityMapper,
            urlShortener, imageHandler, fileHandler, downloadHelper,
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

    verify(remoteAuthServiceFactory).verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString))
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return invalid purchase exception on authenticatePurchase 403 error response`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("error", 403, randomString)))

    wallrDataRepository.authenticatePurchase(randomString, randomString, randomString)
        .test()
        .assertError(InvalidPurchaseException::class.java)

    verify(remoteAuthServiceFactory).verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString))
    verifyIoSchedulerSubscription()
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
    verifyIoSchedulerSubscription()
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
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return true after successfully updating purchase status`() {
    `when`(sharedPrefs.setBoolean(PURCHASE_PREFERENCE_NAME,
        PREMIUM_USER_TAG, true)).thenReturn(true)

    assertEquals(true, wallrDataRepository.updateUserPurchaseStatus())
    verify(sharedPrefs).setBoolean(PURCHASE_PREFERENCE_NAME,
        PREMIUM_USER_TAG, true)
  }

  @Test fun `should return false after unsuccessful update of purchase status`() {
    `when`(sharedPrefs.setBoolean(PURCHASE_PREFERENCE_NAME,
        PREMIUM_USER_TAG, true)).thenReturn(false)

    assertEquals(false, wallrDataRepository.updateUserPurchaseStatus())

    verify(sharedPrefs).setBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, true)
  }

  @Test fun `should return true after checking if user is a premium user`() {
    `when`(sharedPrefs.getBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG,
        false)).thenReturn(true)

    assertEquals(true, wallrDataRepository.isUserPremium())

    verify(sharedPrefs).getBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, false)
  }

  @Test fun `should return false after checking if user is a premium user`() {
    `when`(sharedPrefs.getBoolean(PURCHASE_PREFERENCE_NAME,
        PREMIUM_USER_TAG, false)).thenReturn(false)

    assertEquals(false, wallrDataRepository.isUserPremium())

    verify(sharedPrefs).getBoolean(PURCHASE_PREFERENCE_NAME, PREMIUM_USER_TAG, false)
  }

  @Test fun `should return no result found exception on getPictures call success`() {
    `when`(unsplashClientFactory.getPicturesService(randomString)).thenReturn(
        Single.just(emptyList()))

    wallrDataRepository.getSearchPictures(randomString)
        .test()
        .assertError(NoResultFoundException::class.java)

    verify(unsplashClientFactory).getPicturesService(randomString)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return unable to resolve host exception on getPictures call failure`() {
    `when`(unsplashClientFactory.getPicturesService(randomString)).thenReturn(
        Single.error(Exception(UNABLE_TO_RESOLVE_HOST_EXCEPTION_MESSAGE)))

    wallrDataRepository.getSearchPictures(randomString)
        .test()
        .assertError(UnableToResolveHostException::class.java)

    verify(unsplashClientFactory).getPicturesService(randomString)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return mapped search pictures model list on getPictures call failure`() {
    val unsplashPicturesEntityList = mutableListOf(
        getUnsplashPictureEntityModel())
    val searchPicturesModelList = listOf(getSearchPicturesModel())
    `when`(unsplashPictureEntityMapper.mapFromEntity(unsplashPicturesEntityList)).thenReturn(
        searchPicturesModelList)
    `when`(unsplashClientFactory.getPicturesService(randomString)).thenReturn(
        Single.just(unsplashPicturesEntityList))

    val searchPicturesResult = wallrDataRepository.getSearchPictures(randomString)
        .test()
        .values()[0][0]

    assertTrue(searchPicturesModelList[0] == searchPicturesResult)
    verify(unsplashClientFactory).getPicturesService(randomString)
    verify(unsplashPictureEntityMapper).mapFromEntity(unsplashPicturesEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return explore node reference on getNodeReference call`() {
    stubFirebaseDatabaseNode(CHILD_PATH_EXPLORE)

    val nodeReference = wallrDataRepository.getExploreNodeReference()

    assertTrue(nodeReference == databaseReference)
    verify(firebaseDatabaseHelper).getDatabase()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_EXPLORE)
  }

  @Test fun `should return shortened image link on getShortImageLink call success`() {
    val shortUrl = "Short Url"
    `when`(urlShortener.getShortUrl(randomString)).thenReturn(Single.just(shortUrl))

    wallrDataRepository.getShortImageLink(randomString).test().assertValue(shortUrl)

    verify(urlShortener).getShortUrl(randomString)
    verify(executionThread).ioScheduler
  }

  @Test fun `should return error on getShortImageLink call failure`() {
    `when`(urlShortener.getShortUrl(randomString)).thenReturn(Single.error(Exception()))

    wallrDataRepository.getShortImageLink(randomString).test().assertError(Exception::class.java)

    verify(urlShortener).getShortUrl(randomString)
    verify(executionThread).ioScheduler
  }

  @Test fun `should return NotEnoughFreeSpace exception on getImageBitmap call error`() {
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(false)

    wallrDataRepository.getImageBitmap(randomString).test()
        .assertError(NotEnoughFreeSpaceException::class.java)

    verify(fileHandler).freeSpaceAvailable()
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
    verify(imageHandler).isImageCached(randomString)
    verify(imageHandler).getImageBitmap()
    verifyIoSchedulerSubscription()
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
    verify(imageHandler).fetchImage(randomString)
    verify(imageHandler).isImageCached(randomString)
    verifyIoSchedulerSubscription()
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
    verify(imageHandler).fetchImage(randomString)
    verify(imageHandler).isImageCached(randomString)
    verify(imageHandler).getImageBitmap()
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return Single of bitmap on getCacheImageBitmap call success`() {
    `when`(imageHandler.getImageBitmap()).thenReturn(mockBitmap)

    wallrDataRepository.getCacheImageBitmap().test().assertValue(mockBitmap)

    verify(imageHandler).getImageBitmap()
    verifyComputationSchedulerCall()
  }

  @Test fun `should complete on clearImageCaches call success`() {
    `when`(imageHandler.clearImageCache()).thenReturn(Completable.complete())

    wallrDataRepository.clearImageCaches().test().assertComplete()

    verify(imageHandler).clearImageCache()
    verifyComputationSchedulerCall()
  }

  @Test fun `should invoke cancelImageFetching on cancelImageBitmapFetchOperation call success`() {
    wallrDataRepository.cancelImageBitmapFetchOperation()

    verify(imageHandler).cancelFetchingImage()
  }

  @Test fun `should return image uri on getCacheSourceUri call success`() {
    `when`(imageHandler.getImageUri()).thenReturn(mockUri)

    val uri = wallrDataRepository.getCacheSourceUri()

    assertEquals(mockUri, uri)
    verify(imageHandler).getImageUri()
  }

  @Test fun `should return result destination file uri on getCacheResultUri call success`() {
    `when`(fileHandler.getCacheFileUriForCropping()).thenReturn(mockUri)

    val uri = wallrDataRepository.getCacheResultUri()

    assertEquals(mockUri, uri)
    verify(fileHandler).getCacheFileUriForCropping()
  }

  @Test
  fun `should return Single of Nullpointer exception on getBitmapFromUri call failure due to null uri`() {
    `when`(imageHandler.convertUriToBitmap(mockUri)).thenReturn(
        Single.error(NullPointerException()))

    wallrDataRepository.getBitmapFromUri(mockUri).test()
        .assertError(NullPointerException::class.java)

    verify(imageHandler).convertUriToBitmap(mockUri)
    verifyComputationSchedulerCall()
  }

  @Test fun `should return Single of bitmap on getBitmapFromUri call success`() {
    `when`(imageHandler.convertUriToBitmap(mockUri)).thenReturn(Single.just(mockBitmap))

    wallrDataRepository.getBitmapFromUri(mockUri).test()
        .assertValue(mockBitmap)

    verify(imageHandler).convertUriToBitmap(mockUri)
    verifyComputationSchedulerCall()
  }

  @Test fun `should complete successfully on downloadImage call success`() {
    `when`(downloadHelper.downloadImage(randomString)).thenReturn(Completable.complete())

    wallrDataRepository.downloadImage(randomString).test().assertComplete()

    verify(downloadHelper).downloadImage(randomString)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return true on checkIfDownloadIsInProgress call success`() {
    `when`(downloadHelper.isDownloadEnqueued(randomString)).thenReturn(true)

    assertTrue(wallrDataRepository.checkIfDownloadIsInProgress(randomString))

    verify(downloadHelper).isDownloadEnqueued(randomString)
  }

  @Test fun `should return pair on crystallize image call success`() {
    `when`(imageHandler.convertImageInCacheToLowpoly()).thenReturn(Single.just(mockBitmap))

    val result = wallrDataRepository.crystallizeImage().test().values()[0]

    assertEquals(true, result.first)
    assertEquals(mockBitmap, result.second)
    verify(imageHandler).convertImageInCacheToLowpoly()
    verifyComputationSchedulerCall()
  }

  @Test fun `should complete on saveCrystallizedImageToDownloads call success`() {
    `when`(imageHandler.addCachedImageToDownloads()).thenReturn(Completable.complete())

    wallrDataRepository.saveCachedImageToDownloads().test().assertComplete()

    verify(imageHandler).addCachedImageToDownloads()
    verifyComputationSchedulerCall()
  }

  @Test fun `should return false on isCrystallizeDescriptionShown call success`() {
    `when`(sharedPrefs.getBoolean(IMAGE_PREFERENCE_NAME, CRYSTALLIZE_HINT_DIALOG_SHOWN_BEFORE_TAG))
        .thenReturn(false)

    assertFalse(wallrDataRepository.isCrystallizeDescriptionShown())

    verify(sharedPrefs).getBoolean(IMAGE_PREFERENCE_NAME, CRYSTALLIZE_HINT_DIALOG_SHOWN_BEFORE_TAG)
  }

  @Test fun `should call shared preference on saveCrystallizeDescriptionShown call success`() {
    wallrDataRepository.saveCrystallizeDescriptionShown()

    verify(sharedPrefs).setBoolean(IMAGE_PREFERENCE_NAME, CRYSTALLIZE_HINT_DIALOG_SHOWN_BEFORE_TAG,
        true)
  }

  @Test fun `should complete on saveImageToCollections call success of type wallpaper`() {
    `when`(databaseImageTypeMapper.mapToDatabaseImageType(
        CollectionsImageType.WALLPAPER))
        .thenReturn(WALLPAPER)
    `when`(imageHandler.addImageToCollections(randomString, WALLPAPER)).thenReturn(
        Completable.complete())

    wallrDataRepository.saveImageToCollections(randomString, CollectionsImageType.WALLPAPER)
        .test()
        .assertComplete()

    verify(databaseImageTypeMapper).mapToDatabaseImageType(
        CollectionsImageType.WALLPAPER)
    verify(imageHandler).addImageToCollections(randomString, WALLPAPER)
    verifyComputationSchedulerCall()
  }

  @Test fun `should complete on saveImageToCollections call success of type search`() {
    `when`(databaseImageTypeMapper.mapToDatabaseImageType(
        CollectionsImageType.SEARCH))
        .thenReturn(SEARCH)
    `when`(imageHandler.addImageToCollections(randomString, SEARCH))
        .thenReturn(Completable.complete())

    wallrDataRepository.saveImageToCollections(randomString, CollectionsImageType.SEARCH)
        .test()
        .assertComplete()

    verify(databaseImageTypeMapper).mapToDatabaseImageType(CollectionsImageType.SEARCH)
    verify(imageHandler).addImageToCollections(randomString, SEARCH)
    verifyComputationSchedulerCall()
  }

  @Test fun `should complete on saveImageToCollections call success of type edited`() {
    `when`(databaseImageTypeMapper.mapToDatabaseImageType(
        CollectionsImageType.EDITED))
        .thenReturn(EDITED)
    `when`(imageHandler.addImageToCollections(randomString, EDITED)).thenReturn(
        Completable.complete())

    wallrDataRepository.saveImageToCollections(randomString, CollectionsImageType.EDITED)
        .test()
        .assertComplete()

    verify(databaseImageTypeMapper).mapToDatabaseImageType(CollectionsImageType.EDITED)
    verify(imageHandler).addImageToCollections(randomString, EDITED)
    verifyComputationSchedulerCall()
  }

  @Test fun `should complete on saveImageToCollections call success of type crystallized`() {
    `when`(databaseImageTypeMapper.mapToDatabaseImageType(
        CollectionsImageType.CRYSTALLIZED)).thenReturn(CRYSTALLIZED)
    `when`(imageHandler.addImageToCollections(randomString, CRYSTALLIZED)).thenReturn(
        Completable.complete())

    wallrDataRepository.saveImageToCollections(randomString, CollectionsImageType.CRYSTALLIZED)
        .test()
        .assertComplete()

    verify(databaseImageTypeMapper).mapToDatabaseImageType(
        CollectionsImageType.CRYSTALLIZED)
    verify(imageHandler).addImageToCollections(randomString, CRYSTALLIZED)
    verifyComputationSchedulerCall()
  }

  @Test fun `should complete on saveImageToCollections call success of type minimal color`() {
    `when`(databaseImageTypeMapper.mapToDatabaseImageType(
        CollectionsImageType.MINIMAL_COLOR)).thenReturn(MINIMAL_COLOR)
    `when`(imageHandler.addImageToCollections(randomString, MINIMAL_COLOR)).thenReturn(
        Completable.complete())

    wallrDataRepository.saveImageToCollections(randomString,
        CollectionsImageType.MINIMAL_COLOR)
        .test()
        .assertComplete()

    verify(databaseImageTypeMapper).mapToDatabaseImageType(
        CollectionsImageType.MINIMAL_COLOR)
    verify(imageHandler).addImageToCollections(randomString, MINIMAL_COLOR)
    verifyComputationSchedulerCall()
  }

  @Test fun `should return Single of ImageModel list on getExplorePictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = listOf(getImageModel())
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)).thenReturn(
        imageModelList)
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
    verify(gsonProvider).getGson()
    verifyFirebaseDatabaseHelperCallToFetchDatabaseReference()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_EXPLORE)
    verify(firebasePictureEntityMapper).mapFromEntity(firebaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return Single of ImageModel list on getRecentPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = listOf(getImageModel())
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)).thenReturn(
        imageModelList)
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(databaseReference.child(CHILD_PATH_RECENT)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))
    stubFirebaseDatabaseNode(CHILD_PATH_TOP_PICKS)

    wallrDataRepository.getRecentPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    verify(gsonProvider).getGson()
    verifyFirebaseDatabaseHelperCallToFetchDatabaseReference()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_TOP_PICKS)
    verify(databaseReference).child(CHILD_PATH_RECENT)
    verify(firebasePictureEntityMapper).mapFromEntity(firebaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return Single of ImageModel list on getPopularPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = listOf(getImageModel())
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)).thenReturn(
        imageModelList)
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(databaseReference.child(CHILD_PATH_POPULAR)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))
    stubFirebaseDatabaseNode(CHILD_PATH_TOP_PICKS)

    wallrDataRepository.getPopularPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    verify(gsonProvider).getGson()
    verifyFirebaseDatabaseHelperCallToFetchDatabaseReference()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_TOP_PICKS)
    verify(databaseReference).child(CHILD_PATH_POPULAR)
    verify(firebasePictureEntityMapper).mapFromEntity(firebaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return Single of ImageModel list on getStandoutPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = listOf(getImageModel())
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)).thenReturn(
        imageModelList)
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(databaseReference.child(CHILD_PATH_STANDOUT)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))
    stubFirebaseDatabaseNode(CHILD_PATH_TOP_PICKS)

    wallrDataRepository.getStandoutPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    verify(gsonProvider).getGson()
    verifyFirebaseDatabaseHelperCallToFetchDatabaseReference()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_TOP_PICKS)
    verify(databaseReference).child(CHILD_PATH_STANDOUT)
    verify(firebasePictureEntityMapper).mapFromEntity(firebaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return Single of ImageModel list on getBuildingsPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = listOf(getImageModel())
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)).thenReturn(
        imageModelList)
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(databaseReference.child(CHILD_PATH_BUILDING)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))
    stubFirebaseDatabaseNode(CHILD_PATH_CATEGORIES)

    wallrDataRepository.getBuildingsPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    verify(gsonProvider).getGson()
    verifyFirebaseDatabaseHelperCallToFetchDatabaseReference()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_CATEGORIES)
    verify(databaseReference).child(CHILD_PATH_BUILDING)
    verify(firebasePictureEntityMapper).mapFromEntity(firebaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return Single of ImageModel list on getFoodPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = listOf(getImageModel())
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList))
        .thenReturn(imageModelList)
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(databaseReference.child(CHILD_PATH_FOOD)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))
    stubFirebaseDatabaseNode(CHILD_PATH_CATEGORIES)

    wallrDataRepository.getFoodPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    verify(gsonProvider).getGson()
    verifyFirebaseDatabaseHelperCallToFetchDatabaseReference()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_CATEGORIES)
    verify(databaseReference).child(CHILD_PATH_FOOD)
    verify(firebasePictureEntityMapper).mapFromEntity(firebaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return Single of ImageModel list on getNaturePictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = listOf(getImageModel())
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList))
        .thenReturn(imageModelList)
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(databaseReference.child(CHILD_PATH_NATURE)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))
    stubFirebaseDatabaseNode(CHILD_PATH_CATEGORIES)

    wallrDataRepository.getNaturePictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    verify(gsonProvider).getGson()
    verifyFirebaseDatabaseHelperCallToFetchDatabaseReference()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_CATEGORIES)
    verify(databaseReference).child(CHILD_PATH_NATURE)
    verify(firebasePictureEntityMapper).mapFromEntity(firebaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return Single of ImageModel list on getObjectsPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = listOf(getImageModel())
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList))
        .thenReturn(imageModelList)
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(databaseReference.child(CHILD_PATH_OBJECT)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))
    stubFirebaseDatabaseNode(CHILD_PATH_CATEGORIES)

    wallrDataRepository.getObjectsPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    verify(gsonProvider).getGson()
    verifyFirebaseDatabaseHelperCallToFetchDatabaseReference()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_CATEGORIES)
    verify(databaseReference).child(CHILD_PATH_OBJECT)
    verify(firebasePictureEntityMapper).mapFromEntity(firebaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return Single of ImageModel list on getPeoplePictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = listOf(getImageModel())
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList))
        .thenReturn(imageModelList)
    `when`(databaseReference.child(CHILD_PATH_PEOPLE)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))
    stubFirebaseDatabaseNode(CHILD_PATH_CATEGORIES)

    wallrDataRepository.getPeoplePictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    verify(gsonProvider).getGson()
    verifyFirebaseDatabaseHelperCallToFetchDatabaseReference()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_CATEGORIES)
    verify(databaseReference).child(CHILD_PATH_PEOPLE)
    verify(firebasePictureEntityMapper).mapFromEntity(firebaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return Single of ImageModel list on getTechnologyPictures call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = listOf(getImageModel())
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    val gson = Gson()
    val gsonString = gson.toJson(firebaseImageEntity)
    map[randomString] = gsonString
    `when`(firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)).thenReturn(
        imageModelList)
    `when`(gsonProvider.getGson()).thenReturn(gson)
    `when`(databaseReference.child(CHILD_PATH_TECHNOLOGY)).thenReturn(databaseReference)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))
    stubFirebaseDatabaseNode(CHILD_PATH_CATEGORIES)

    wallrDataRepository.getTechnologyPictures().subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(FIREBASE_TIMEOUT_DURATION.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    verify(gsonProvider).getGson()
    verifyFirebaseDatabaseHelperCallToFetchDatabaseReference()
    verify(firebaseDatabase).getReference(FIREBASE_DATABASE_PATH)
    verify(databaseReference).child(CHILD_PATH_CATEGORIES)
    verify(databaseReference).child(CHILD_PATH_TECHNOLOGY)
    verify(firebasePictureEntityMapper).mapFromEntity(firebaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return true on isCustomColorListPresent call success`() {
    `when`(sharedPrefs.getBoolean(IMAGE_PREFERENCE_NAME,
        CUSTOM_MINIMAL_COLOR_LIST_AVAILABLE_TAG)).thenReturn(true)

    assertTrue(wallrDataRepository.isCustomMinimalColorListPresent())

    verify(sharedPrefs).getBoolean(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_AVAILABLE_TAG)
  }

  @Test fun `should return single of list of string on getCustomColorList call success`() {
    val list = listOf(randomString)
    `when`(minimalColorHelper.getCustomColors()).thenReturn(Single.just(list))

    wallrDataRepository.getCustomMinimalColorList().test().assertValue(list)

    verify(minimalColorHelper).getCustomColors()
    verifyIoSchedulerSubscription()
  }

  @Test fun `should return single of list of string on getDefaultColorList call success`() {
    val list = listOf(randomString)
    `when`(minimalColorHelper.getDefaultColors()).thenReturn(Single.just(list))

    wallrDataRepository.getDefaultMinimalColorList().test().assertValue(list)

    verify(minimalColorHelper).getDefaultColors()
    verifyIoSchedulerSubscription()
  }

  @Test fun `should complete saveCustomMinimalColorList call success`() {
    val list = listOf(randomString)
    val gsonString = Gson().toJson(list)
    `when`(gsonProvider.getGson()).thenReturn(Gson())
    `when`(sharedPrefs.setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG, gsonString))
        .thenReturn(true)

    wallrDataRepository.saveCustomMinimalColorList(list).test().assertComplete()

    verify(gsonProvider).getGson()
    verify(sharedPrefs).setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
        gsonString)
    verifyIoSchedulerSubscription()
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

    verify(gsonProvider).getGson()
    verify(minimalColorHelper).cacheDeletedItems(selectedIndices)
    verify(sharedPrefs).setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
        gsonString)
    verify(sharedPrefs).setBoolean(IMAGE_PREFERENCE_NAME,
        CUSTOM_MINIMAL_COLOR_LIST_AVAILABLE_TAG, true)
    verifyComputationSchedulerCall()
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

    verify(gsonProvider).getGson()
    verify(minimalColorHelper).cacheDeletedItems(selectedIndices)
    verify(sharedPrefs).setString(IMAGE_PREFERENCE_NAME, CUSTOM_MINIMAL_COLOR_LIST_TAG,
        gsonString)
    verifyComputationSchedulerCall()
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

    verify(gsonProvider).getGson()
    verify(minimalColorHelper).getCustomColors()
    verify(minimalColorHelper).getDeletedItemsFromCache()
    verify(sharedPrefs).setString(IMAGE_PREFERENCE_NAME,
        CUSTOM_MINIMAL_COLOR_LIST_TAG, Gson().toJson(modifiedList))
    verifyComputationSchedulerCall()
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
    verifyComputationSchedulerCall()
  }

  @Test fun `should return single of uri on geShareableImageUri call success`() {
    `when`(imageHandler.getShareableUri()).thenReturn(Single.just(mockUri))

    val result = wallrDataRepository.getShareableImageUri().test().values()[0]

    assertEquals(mockUri, result)
    verify(imageHandler).getShareableUri()
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return NotEnoughFreeSpaceException on getSingleColorBitmap call failure due to low storage space`() {
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(false)

    wallrDataRepository.getSingleColorBitmap(randomString).test()
        .assertError(NotEnoughFreeSpaceException::class.java)

    verify(fileHandler).freeSpaceAvailable()
  }

  @Test
  fun `should return single of bitmap on getSingleColorBitmap call success`() {
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(true)
    `when`(imageHandler.getSingleColorBitmap(randomString)).thenReturn(Single.just(mockBitmap))

    val result = wallrDataRepository.getSingleColorBitmap(randomString).test()
        .values()[0]

    assertEquals(mockBitmap, result)
    verify(fileHandler).freeSpaceAvailable()
    verify(imageHandler).getSingleColorBitmap(randomString)
    verifyComputationSchedulerCall()
  }

  @Test
  fun `should return NotEnoughFreeSpaceException on getMultiColorBitmap call of type Material failure due to low storage space`() {
    val list = listOf(randomString)
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(false)

    wallrDataRepository.getMultiColorBitmap(list, MATERIAL).test()
        .assertError(NotEnoughFreeSpaceException::class.java)

    verify(fileHandler).freeSpaceAvailable()
  }

  @Test
  fun `should return single of bitmap on getMultiColorBitmap  call of type Material success`() {
    val list = listOf(randomString)
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(true)
    `when`(imageHandler.getMultiColorBitmap(list, MATERIAL)).thenReturn(Single.just(mockBitmap))

    val result = wallrDataRepository.getMultiColorBitmap(list, MATERIAL).test()
        .values()[0]

    assertEquals(mockBitmap, result)
    verify(fileHandler).freeSpaceAvailable()
    verify(imageHandler).getMultiColorBitmap(list, MATERIAL)
    verifyComputationSchedulerCall()
  }

  @Test
  fun `should return NotEnoughFreeSpaceException on getMultiColorBitmap call of type Gradient failure due to low storage space`() {
    val list = listOf(randomString)
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(false)

    wallrDataRepository.getMultiColorBitmap(list, GRADIENT).test()
        .assertError(NotEnoughFreeSpaceException::class.java)

    verify(fileHandler).freeSpaceAvailable()
  }

  @Test
  fun `should return single of bitmap on getMultiColorBitmap  call of type Gradient success`() {
    val list = listOf(randomString)
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(true)
    `when`(imageHandler.getMultiColorBitmap(list, GRADIENT)).thenReturn(Single.just(mockBitmap))

    val result = wallrDataRepository.getMultiColorBitmap(list, GRADIENT).test()
        .values()[0]

    assertEquals(mockBitmap, result)
    verify(fileHandler).freeSpaceAvailable()
    verify(imageHandler).getMultiColorBitmap(list, GRADIENT)
    verifyComputationSchedulerCall()
  }

  @Test
  fun `should return NotEnoughFreeSpaceException on getMultiColorBitmap call of type Plasma failure due to low storage space`() {
    val list = listOf(randomString)
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(false)

    wallrDataRepository.getMultiColorBitmap(list, PLASMA).test()
        .assertError(NotEnoughFreeSpaceException::class.java)

    verify(fileHandler).freeSpaceAvailable()
  }

  @Test
  fun `should return single of bitmap on getMultiColorBitmap  call of type Plasma success`() {
    val list = listOf(randomString)
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(true)
    `when`(imageHandler.getMultiColorBitmap(list, PLASMA)).thenReturn(Single.just(mockBitmap))

    val result = wallrDataRepository.getMultiColorBitmap(list, PLASMA).test()
        .values()[0]

    assertEquals(mockBitmap, result)
    verify(fileHandler).freeSpaceAvailable()
    verify(imageHandler).getMultiColorBitmap(list, PLASMA)
    verifyComputationSchedulerCall()
  }

  @Test
  fun `should return single of list of collections image model on getImagesInCollection call success`() {
    val collectionsDatabaseImageEntityList =
        listOf(getCollectionsDatabaseImageEntity())
    `when`(imageHandler.getAllImagesInCollection()).thenReturn(
        Single.just(collectionsDatabaseImageEntityList))

    val result = wallrDataRepository.getImagesInCollection().test().values()[0]

    assertEquals(collectionsDatabaseImageEntityMapper
        .mapFromEntity(collectionsDatabaseImageEntityList), result)
    verify(collectionsDatabaseImageEntityMapper, times(2))
        .mapFromEntity(collectionsDatabaseImageEntityList)
    verify(imageHandler).getAllImagesInCollection()
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return single of error image model on getImagesInCollection call failure`() {
    `when`(imageHandler.getAllImagesInCollection()).thenReturn(
        Single.error(Exception()))

    wallrDataRepository.getImagesInCollection().test().assertError(Exception::class.java)

    verify(imageHandler).getAllImagesInCollection()
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return single of list of collections image model on addImagesToCollection call success`() {
    val mockUriList = listOf(mockUri)
    val collectionsImageModelList = listOf(getCollectionsImageModel())
    val collectionsDatabaseImageEntityList =
        listOf(getCollectionsDatabaseImageEntity())
    `when`(collectionsDatabaseImageEntityMapper.mapFromEntity(collectionsDatabaseImageEntityList))
        .thenReturn(collectionsImageModelList)
    `when`(imageHandler.addExternalImageToCollection(mockUriList)).thenReturn(
        Completable.complete())
    `when`(imageHandler.getAllImagesInCollection()).thenReturn(
        Single.just(collectionsDatabaseImageEntityList))

    val result = wallrDataRepository.addImagesToCollection(mockUriList).test().values()[0]

    assertEquals(collectionsImageModelList, result)
    verify(collectionsDatabaseImageEntityMapper).mapFromEntity(collectionsDatabaseImageEntityList)
    verify(imageHandler).addExternalImageToCollection(mockUriList)
    verify(imageHandler).getAllImagesInCollection()
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return error on addImagesToCollection call failure`() {
    val mockUriList = listOf(mockUri)
    val collectionsDatabaseImageEntityList =
        listOf(getCollectionsDatabaseImageEntity())
    `when`(imageHandler.addExternalImageToCollection(mockUriList)).thenReturn(
        Completable.error(Exception()))
    `when`(imageHandler.getAllImagesInCollection()).thenReturn(
        Single.just(collectionsDatabaseImageEntityList))

    wallrDataRepository.addImagesToCollection(mockUriList).test().assertError(Exception::class.java)

    verify(imageHandler).addExternalImageToCollection(mockUriList)
    verify(imageHandler).getAllImagesInCollection()
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return single of list of collections image model on reorderImagesInCollectionDatabase call success`() {
    val collectionImageModelList = listOf(getCollectionsImageModel())
    val collectionDatabaseImageEntityList =
        listOf(getCollectionsDatabaseImageEntity())
    `when`(collectionsDatabaseImageEntityMapper.mapToEntity(collectionImageModelList))
        .thenReturn(collectionDatabaseImageEntityList)
    `when`(collectionsDatabaseImageEntityMapper.mapFromEntity(collectionDatabaseImageEntityList))
        .thenReturn(collectionImageModelList)
    `when`(imageHandler.reorderImagesInCollection(collectionDatabaseImageEntityList)).thenReturn(
        Single.just(collectionDatabaseImageEntityList))

    val result = wallrDataRepository.reorderImagesInCollectionDatabase(collectionImageModelList)
        .test().values()[0]

    assertEquals(collectionImageModelList, result)
    verify(collectionsDatabaseImageEntityMapper).mapToEntity(collectionImageModelList)
    verify(collectionsDatabaseImageEntityMapper).mapFromEntity(collectionDatabaseImageEntityList)
    verify(imageHandler).reorderImagesInCollection(collectionDatabaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return single of error image model on reorderImagesInCollectionDatabase call failure`() {
    val collectionImagesModelList = listOf(getCollectionsImageModel())
    val collectionDatabaseImageEntityList =
        listOf(getCollectionsDatabaseImageEntity())
    `when`(collectionsDatabaseImageEntityMapper.mapToEntity(collectionImagesModelList))
        .thenReturn(collectionDatabaseImageEntityList)
    `when`(imageHandler.reorderImagesInCollection(collectionDatabaseImageEntityList)).thenReturn(
        Single.error(Exception()))

    wallrDataRepository.reorderImagesInCollectionDatabase(collectionImagesModelList).test()
        .assertError(Exception::class.java)

    verify(collectionsDatabaseImageEntityMapper).mapToEntity(collectionImagesModelList)
    verify(imageHandler).reorderImagesInCollection(collectionDatabaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return single of list of collections image model on deleteImageFromCollection call success`() {
    val collectionImageModelList = listOf(getCollectionsImageModel())
    val collectionDatabaseImageEntityList =
        listOf(getCollectionsDatabaseImageEntity())
    `when`(collectionsDatabaseImageEntityMapper.mapToEntity(collectionImageModelList))
        .thenReturn(collectionDatabaseImageEntityList)
    `when`(collectionsDatabaseImageEntityMapper.mapFromEntity(collectionDatabaseImageEntityList))
        .thenReturn(collectionImageModelList)
    `when`(imageHandler.deleteImagesInCollection(collectionDatabaseImageEntityList)).thenReturn(
        Single.just(collectionDatabaseImageEntityList))

    val result = wallrDataRepository.deleteImageFromCollection(collectionImageModelList)
        .test().values()[0]

    assertEquals(collectionImageModelList, result)
    verify(collectionsDatabaseImageEntityMapper).mapToEntity(collectionImageModelList)
    verify(collectionsDatabaseImageEntityMapper).mapFromEntity(collectionDatabaseImageEntityList)
    verify(imageHandler).deleteImagesInCollection(collectionDatabaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return single of error image model on deleteImageFromCollection call failure`() {
    val collectionImagesModelList = listOf(getCollectionsImageModel())
    val collectionDatabaseImageEntityList =
        listOf(getCollectionsDatabaseImageEntity())
    `when`(collectionsDatabaseImageEntityMapper.mapToEntity(collectionImagesModelList))
        .thenReturn(collectionDatabaseImageEntityList)
    `when`(imageHandler.deleteImagesInCollection(collectionDatabaseImageEntityList)).thenReturn(
        Single.error(Exception()))

    wallrDataRepository.deleteImageFromCollection(collectionImagesModelList).test()
        .assertError(Exception::class.java)

    verify(collectionsDatabaseImageEntityMapper).mapToEntity(collectionImagesModelList)
    verify(imageHandler).deleteImagesInCollection(collectionDatabaseImageEntityList)
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return true on getAutomaticWallpaperChangerState call success as wallpaper changer is enabled`() {
    `when`(sharedPrefs.getBoolean(IMAGE_PREFERENCE_NAME,
        AUTOMATIC_WALLPAPER_CHANGER_STATE_TAG)).thenReturn(true)

    assertTrue(wallrDataRepository.getAutomaticWallpaperChangerState())

    verify(sharedPrefs).getBoolean(IMAGE_PREFERENCE_NAME,
        AUTOMATIC_WALLPAPER_CHANGER_STATE_TAG)
  }

  @Test
  fun `should return false on getAutomaticWallpaperChangerState call success as wallpaper changer is disabled`() {
    `when`(sharedPrefs.getBoolean(IMAGE_PREFERENCE_NAME,
        AUTOMATIC_WALLPAPER_CHANGER_STATE_TAG)).thenReturn(false)

    assertFalse(wallrDataRepository.getAutomaticWallpaperChangerState())

    verify(sharedPrefs).getBoolean(IMAGE_PREFERENCE_NAME,
        AUTOMATIC_WALLPAPER_CHANGER_STATE_TAG)
  }

  @Test
  fun `should return true on isCollectionReorderHintDisplayedBefore call success as hint was displayed earlier once`() {
    `when`(sharedPrefs.getBoolean(HINT_PREFERENCE_NAME,
        COLLECTION_IMAGE_REORDER_HINT_PREFERENCE_TAG)).thenReturn(true)

    assertTrue(wallrDataRepository.isCollectionReorderHintDisplayedBefore())

    verify(sharedPrefs).getBoolean(HINT_PREFERENCE_NAME,
        COLLECTION_IMAGE_REORDER_HINT_PREFERENCE_TAG)
  }

  @Test
  fun `should return false on isCollectionReorderHintDisplayedBefore call success as hint was never displayed`() {
    `when`(sharedPrefs.getBoolean(HINT_PREFERENCE_NAME,
        COLLECTION_IMAGE_REORDER_HINT_PREFERENCE_TAG)).thenReturn(false)

    assertFalse(wallrDataRepository.isCollectionReorderHintDisplayedBefore())

    verify(sharedPrefs).getBoolean(HINT_PREFERENCE_NAME,
        COLLECTION_IMAGE_REORDER_HINT_PREFERENCE_TAG)
  }

  @Test
  fun `should save reorder hint shown state on saveCollectionReorderHintShownState call success`() {
    wallrDataRepository.saveCollectionReorderHintShownState()

    verify(sharedPrefs).setBoolean(HINT_PREFERENCE_NAME,
        COLLECTION_IMAGE_REORDER_HINT_PREFERENCE_TAG,
        true)
  }

  @Test fun `should return bitmap on getBitmapFromDatabaseImage call success`() {
    val collectionsImageModelList = listOf(getCollectionsImageModel())
    val collectionDatabaseImageEntityList =
        listOf(getCollectionsDatabaseImageEntity())
    `when`(collectionsDatabaseImageEntityMapper.mapToEntity(collectionsImageModelList))
        .thenReturn(collectionDatabaseImageEntityList)
    `when`(imageHandler.getImageBitmap(collectionDatabaseImageEntityList.first().path))
        .thenReturn(mockBitmap)

    wallrDataRepository.getBitmapFromDatabaseImage(collectionsImageModelList.first())

    verify(collectionsDatabaseImageEntityMapper).mapToEntity(collectionsImageModelList)
    verify(imageHandler).getImageBitmap(collectionDatabaseImageEntityList.first().path)
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return single of list of collections image model on saveCrystallizedImageInDatabase call success`() {
    val collectionImageModelList = listOf(getCollectionsImageModel())
    val collectionDatabaseImageEntityList =
        listOf(getCollectionsDatabaseImageEntity())
    `when`(collectionsDatabaseImageEntityMapper.mapToEntity(collectionImageModelList))
        .thenReturn(collectionDatabaseImageEntityList)
    `when`(collectionsDatabaseImageEntityMapper.mapFromEntity(collectionDatabaseImageEntityList))
        .thenReturn(collectionImageModelList)
    `when`(imageHandler.convertAndCacheLowpolyImage(collectionDatabaseImageEntityList.first().path,
        CRYSTALLIZED))
        .thenReturn(Completable.complete())
    `when`(imageHandler.addImageToCollections(collectionDatabaseImageEntityList.first().path,
        CRYSTALLIZED)).thenReturn(Completable.complete())
    `when`(imageHandler.getAllImagesInCollection())
        .thenReturn(Single.just(collectionDatabaseImageEntityList))

    val result =
        wallrDataRepository.saveCrystallizedImageInDatabase(collectionImageModelList.first())
            .test().values()[0]

    val inOrder = inOrder(imageHandler)
    assertEquals(collectionImageModelList, result)
    verify(collectionsDatabaseImageEntityMapper).mapToEntity(collectionImageModelList)
    verify(collectionsDatabaseImageEntityMapper).mapFromEntity(collectionDatabaseImageEntityList)
    inOrder.verify(imageHandler)
        .convertAndCacheLowpolyImage(collectionDatabaseImageEntityList.first().path, CRYSTALLIZED)
    inOrder.verify(imageHandler)
        .addImageToCollections(collectionDatabaseImageEntityList.first().path,
            CRYSTALLIZED)
    inOrder.verify(imageHandler).getAllImagesInCollection()
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return single already present in collection error on saveCrystallizedImageInDatabase call failure`() {
    val collectionImageModelList = listOf(getCollectionsImageModel())
    val collectionDatabaseImageEntityList =
        listOf(getCollectionsDatabaseImageEntity())
    `when`(collectionsDatabaseImageEntityMapper.mapToEntity(collectionImageModelList))
        .thenReturn(collectionDatabaseImageEntityList)
    `when`(imageHandler.convertAndCacheLowpolyImage(collectionDatabaseImageEntityList.first().path,
        CRYSTALLIZED)).thenReturn(Completable.error(AlreadyPresentInCollectionException()))
    `when`(imageHandler.addImageToCollections(collectionDatabaseImageEntityList.first().path,
        CRYSTALLIZED)).thenReturn(Completable.complete())
    `when`(imageHandler.getAllImagesInCollection())
        .thenReturn(Single.just(collectionDatabaseImageEntityList))

    wallrDataRepository.saveCrystallizedImageInDatabase(collectionImageModelList.first())
        .test().assertError(AlreadyPresentInCollectionException::class.java)

    val inOrder = inOrder(imageHandler)
    verify(collectionsDatabaseImageEntityMapper).mapToEntity(collectionImageModelList)
    inOrder.verify(imageHandler)
        .convertAndCacheLowpolyImage(collectionDatabaseImageEntityList.first().path, CRYSTALLIZED)
    inOrder.verify(imageHandler)
        .addImageToCollections(collectionDatabaseImageEntityList.first().path,
            CRYSTALLIZED)
    inOrder.verify(imageHandler).getAllImagesInCollection()
    verifyIoSchedulerSubscription()
  }

  @Test
  fun `should return interval at which wallpaper changer runs on getWallpaperChangerInterval call success`() {
    val interval = Random().nextLong()
    `when`(sharedPrefs.getLong(IMAGE_PREFERENCE_NAME,
        AUTOMATIC_WALLPAPER_CHANGER_INTERVAL_TAG)).thenReturn(interval)

    assertEquals(interval, wallrDataRepository.getWallpaperChangerInterval())

    verify(sharedPrefs).getLong(IMAGE_PREFERENCE_NAME,
        AUTOMATIC_WALLPAPER_CHANGER_INTERVAL_TAG)
  }

  @Test fun `should set wallpaper changer interval on setWallpaperChangerInterval call success`() {
    val interval = Random().nextLong()

    wallrDataRepository.setWallpaperChangerInterval(interval)

    verify(sharedPrefs).setLong(IMAGE_PREFERENCE_NAME,
        AUTOMATIC_WALLPAPER_CHANGER_INTERVAL_TAG, interval)
  }

  @Test
  fun `should return uid on getLastUsedWallpaperUid call success`() {
    val uid = Random().nextLong()
    `when`(sharedPrefs.getLong(IMAGE_PREFERENCE_NAME,
        AUTOMATIC_WALLPAPER_CHANGER_LAST_USED_WALLPAPER_UID_TAG)).thenReturn(uid)

    assertEquals(uid, wallrDataRepository.getLastUsedWallpaperUid())

    verify(sharedPrefs).getLong(IMAGE_PREFERENCE_NAME,
        AUTOMATIC_WALLPAPER_CHANGER_LAST_USED_WALLPAPER_UID_TAG)
  }

  @Test fun `should save uid on setLastUsedWallpaperUid call success`() {
    val uid = Random().nextLong()

    wallrDataRepository.setLastUsedWallpaperUid(uid)

    verify(sharedPrefs).setLong(IMAGE_PREFERENCE_NAME,
        AUTOMATIC_WALLPAPER_CHANGER_LAST_USED_WALLPAPER_UID_TAG, uid)
  }

  @After fun tearDown() {
    verifyNoMoreInteractions(
        executionThread,
        sharedPrefs,
        remoteAuthServiceFactory,
        unsplashClientFactory,
        firebaseDatabaseHelper,
        databaseReference,
        firebaseDatabase,
        urlShortener,
        imageHandler,
        fileHandler,
        downloadHelper,
        minimalColorHelper,
        mockBitmap,
        mockUri,
        gsonProvider,
        collectionsDatabaseImageEntityMapper,
        databaseImageTypeMapper,
        unsplashPictureEntityMapper,
        firebasePictureEntityMapper)
  }

  private fun stubFirebaseDatabaseNode(childPath: String) {
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabase.getReference(FIREBASE_DATABASE_PATH)).thenReturn(
        databaseReference)
    `when`(databaseReference.child(childPath)).thenReturn(databaseReference)
  }

  private fun verifyIoSchedulerSubscription() {
    verify(executionThread).ioScheduler
  }

  private fun verifyComputationSchedulerCall() {
    verify(executionThread).computationScheduler
  }

  private fun verifyFirebaseDatabaseHelperCallToFetchDatabaseReference() {
    verify(firebaseDatabaseHelper).getDatabase()
    verify(firebaseDatabaseHelper).fetch(databaseReference)
  }

}
