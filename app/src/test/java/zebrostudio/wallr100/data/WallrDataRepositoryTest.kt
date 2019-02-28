package zebrostudio.wallr100.data

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.pddstudio.urlshortener.URLShortener
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.datafactory.UnsplashPictureEntityModelFactory
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.NotEnoughFreeSpaceException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapper
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapper
import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class WallrDataRepositoryTest {

  @get:Rule val trampolineSchedulerRule = TrampolineSchedulerRule()

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
  @Mock lateinit var mockBitmap: Bitmap
  @Mock lateinit var mockUri: Uri
  private lateinit var unsplashPictureEntityMapper: UnsplashPictureEntityMapper
  private lateinit var firebasePictureEntityMapper: FirebasePictureEntityMapper
  private lateinit var wallrDataRepository: WallrDataRepository
  private val randomString = randomUUID().toString()
  private val dummyInt = 500 // to force some error other than 403 or 404
  private val unableToResolveHostExceptionMessage = "Unable to resolve host " +
      "\"api.unsplash.com\": No address associated with hostname"
  private val purchasePreferenceName = "PURCHASE_PREF"
  private val premiumUserTag = "premium_user"
  private val firebaseDatabasePath = "wallr"
  private val childPathExplore = "explore"
  private val childPathCategories = "categories"
  private val childPathTopPicks = "collections"
  private val childPathRecent = "recent"
  private val childPathPopular = "popular"
  private val childPathStandout = "standout"
  private val childPathBuilding = "building"
  private val childPathFood = "food"
  private val childPathNature = "nature"
  private val childPathObject = "object"
  private val childPathPeople = "people"
  private val childPathTechnology = "technology"
  private val firebaseTimeoutDuration = 15
  private val downloadProgressCompletedValue: Long = 100
  private val downloadProgressCompleteUpTo99: Long = 99

  @Before
  fun setup() {
    unsplashPictureEntityMapper = UnsplashPictureEntityMapper()
    firebasePictureEntityMapper = FirebasePictureEntityMapper()
    wallrDataRepository =
        WallrDataRepository(remoteAuthServiceFactory, unsplashClientFactory, sharedPrefs,
            unsplashPictureEntityMapper, firebaseDatabaseHelper, firebasePictureEntityMapper,
            urlShortener, imageHandler, fileHandler, downloadHelper)
  }

  @Test fun `should return single on server success response`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("success", dummyInt, randomString)))

    wallrDataRepository.authenticatePurchase(randomString, randomString, randomString)
        .test()
        .assertComplete()
  }

  @Test
  fun `should return invalid purchase exception on authenticatePurchase 403 error response`() {
    `when`(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(randomString, randomString, randomString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("error", 403, randomString)))

    wallrDataRepository.authenticatePurchase(randomString, randomString, randomString)
        .test()
        .assertError(InvalidPurchaseException::class.java)
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
  }

  @Test fun `should return true after successfully updating purchase status`() {
    `when`(sharedPrefs.setBoolean(purchasePreferenceName,
        premiumUserTag, true)).thenReturn(true)

    assertEquals(true, wallrDataRepository.updateUserPurchaseStatus())
  }

  @Test fun `should return false after unsuccessful update of purchase status`() {
    `when`(sharedPrefs.setBoolean(purchasePreferenceName,
        premiumUserTag, true)).thenReturn(false)

    assertEquals(false, wallrDataRepository.updateUserPurchaseStatus())

    verify(sharedPrefs).setBoolean(purchasePreferenceName, premiumUserTag, true)
    verifyNoMoreInteractions(sharedPrefs)
  }

  @Test fun `should return true after checking if user is a premium user`() {
    `when`(sharedPrefs.getBoolean(purchasePreferenceName,
        premiumUserTag, false)).thenReturn(true)

    assertEquals(true, wallrDataRepository.isUserPremium())

    verify(sharedPrefs).getBoolean(purchasePreferenceName, premiumUserTag, false)
    verifyNoMoreInteractions(sharedPrefs)
  }

  @Test fun `should return false after checking if user is a premium user`() {
    `when`(sharedPrefs.getBoolean(purchasePreferenceName,
        premiumUserTag, false)).thenReturn(false)

    assertEquals(false, wallrDataRepository.isUserPremium())

    verify(sharedPrefs).getBoolean(purchasePreferenceName, premiumUserTag, false)
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
  }

  @Test fun `should return unable to resolve host exception on getPictures call failure`() {
    `when`(unsplashClientFactory.getPicturesService(randomString)).thenReturn(
        Single.error(Exception(unableToResolveHostExceptionMessage)))

    wallrDataRepository.getSearchPictures(randomString)
        .test()
        .assertError(UnableToResolveHostException::class.java)

    verify(unsplashClientFactory).getPicturesService(randomString)
    verifyNoMoreInteractions(unsplashClientFactory)
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
  }

  @Test fun `should return explore node reference on getNodeReference call`() {
    stubFirebaseDatabaseNode(childPathExplore)

    val nodeReference = wallrDataRepository.getExploreNodeReference()

    assertTrue(nodeReference == databaseReference)
    verify(firebaseDatabaseHelper, times(3)).getDatabase()
    verifyNoMoreInteractions(firebaseDatabaseHelper)
  }

  @Test fun `should return top picks node reference on getNodeReference call`() {
    stubFirebaseDatabaseNode(childPathTopPicks)

    val nodeReference = wallrDataRepository.getTopPicksNodeReference()

    assertTrue(nodeReference == databaseReference)
    verify(firebaseDatabaseHelper, times(3)).getDatabase()
    verifyNoMoreInteractions(firebaseDatabaseHelper)
  }

  @Test fun `should return categories node reference on getNodeReference call`() {
    stubFirebaseDatabaseNode(childPathCategories)

    val nodeReference = wallrDataRepository.getCategoriesNodeReference()

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

    assertEquals(resultImageDownloadModel.progress, downloadProgressCompleteUpTo99)
    assertEquals(resultImageDownloadModel.imageBitmap, null)
    assertEquals(resultImageDownloadModelCompleted.progress, downloadProgressCompletedValue)
    assertEquals(resultImageDownloadModelCompleted.imageBitmap, mockBitmap)

    verify(fileHandler).freeSpaceAvailable()
    verifyNoMoreInteractions(fileHandler)
    verify(imageHandler).isImageCached(randomString)
    verify(imageHandler).getImageBitmap()
    verifyNoMoreInteractions(imageHandler)
  }

  @Test
  fun `should return imageDownloadModel with only progress on getImageBitmap call success and download in progress`() {
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(true)
    `when`(imageHandler.isImageCached(randomString)).thenReturn(false)
    `when`(imageHandler.fetchImage(randomString)).thenReturn(
        Observable.just(downloadProgressCompleteUpTo99))

    val resultImageDownloadModel =
        wallrDataRepository.getImageBitmap(randomString).test().values()[0]

    assertEquals(resultImageDownloadModel.progress, downloadProgressCompleteUpTo99)
    assertEquals(resultImageDownloadModel.imageBitmap, null)
    verify(fileHandler).freeSpaceAvailable()
    verifyNoMoreInteractions(fileHandler)
    verify(imageHandler).fetchImage(randomString)
    verify(imageHandler).isImageCached(randomString)
    verifyNoMoreInteractions(imageHandler)
  }

  @Test
  fun `should return imageDownloadProgress with progress and bitmap on getImageBitmap call success and download is completed`() {
    `when`(fileHandler.freeSpaceAvailable()).thenReturn(true)
    `when`(imageHandler.isImageCached(randomString)).thenReturn(false)
    `when`(imageHandler.fetchImage(randomString)).thenReturn(
        Observable.just(downloadProgressCompletedValue))
    `when`(imageHandler.getImageBitmap()).thenReturn(mockBitmap)

    val resultImageDownloadModel =
        wallrDataRepository.getImageBitmap(randomString).test().values()[0]

    assertEquals(resultImageDownloadModel.progress, downloadProgressCompletedValue)
    assertEquals(resultImageDownloadModel.imageBitmap, mockBitmap)
    verify(fileHandler).freeSpaceAvailable()
    verifyNoMoreInteractions(fileHandler)
    verify(imageHandler).fetchImage(randomString)
    verify(imageHandler).isImageCached(randomString)
    verify(imageHandler).getImageBitmap()
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
  }

  @Test fun `should complete successfully on downloadImage call success`() {
    `when`(downloadHelper.downloadImage(randomString)).thenReturn(Completable.complete())

    wallrDataRepository.downloadImage(randomString).test().assertComplete()

    verify(downloadHelper).downloadImage(randomString)
    verifyNoMoreInteractions(downloadHelper)
  }

  @Test fun `should return true on checkIfDownloadIsInProgress call success`() {
    `when`(downloadHelper.isDownloadEnqueued(randomString)).thenReturn(true)

    assertTrue(wallrDataRepository.checkIfDownloadIsInProgress(randomString))

    verify(downloadHelper).isDownloadEnqueued(randomString)
    verifyNoMoreInteractions(downloadHelper)
  }

  /* Need to properly implement timeout for Rx Java

  @Test fun `should return Single of ImageModel list on getPicturesFromFirebase call success`() {
    val map = hashMapOf<String, String>()
    val firebaseImageEntity = FirebaseImageEntityModelFactory.getFirebaseImageEntity()
    val firebaseImageEntityList = listOf(firebaseImageEntity)
    val imageModelList = firebasePictureEntityMapper.mapFromEntity(firebaseImageEntityList)
    val testScheduler = TestScheduler()
    val testObserver = TestObserver<Any>()
    map[randomString] = Gson().toJson(firebaseImageEntity)
    `when`(firebaseDatabaseHelper.fetch(databaseReference)).thenReturn(Single.just(map))

    wallrDataRepository.getPicturesFromFirebase(databaseReference).subscribeOn(testScheduler)
        .subscribe(testObserver)
    testScheduler.advanceTimeBy(firebaseTimeoutDuration.toLong(), TimeUnit.SECONDS)

    testObserver.assertValue(imageModelList)
    verify(firebaseDatabaseHelper).fetch(databaseReference)
    verifyNoMoreInteractions(firebaseDatabaseHelper)
  }*/

  private fun stubFirebaseDatabaseNode(childPath: String) {
    `when`(firebaseDatabaseHelper.getDatabase()).thenReturn(firebaseDatabase)
    `when`(firebaseDatabaseHelper.getDatabase().getReference(firebaseDatabasePath)).thenReturn(
        databaseReference)
    `when`(firebaseDatabaseHelper.getDatabase().getReference(firebaseDatabasePath)
        .child(childPath)).thenReturn(databaseReference)
  }

}