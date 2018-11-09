package zebrostudio.wallr100.data

import com.google.firebase.database.DatabaseReference
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import org.mockito.Mock
import org.mockito.Mockito.`when`
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.datafactory.FirebaseImageEntityModelFactory
import zebrostudio.wallr100.data.datafactory.UnsplashPictureEntityModelFactory
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.UnableToResolveHostException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.data.mapper.FirebasePictureEntityMapper
import zebrostudio.wallr100.data.mapper.UnsplashPictureEntityMapper
import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.lang.Exception
import java.util.UUID.*

@RunWith(MockitoJUnitRunner::class)
class WallrDataRepositoryTest {

  @get:Rule val trampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock lateinit var sharedPrefs: SharedPrefsHelper
  @Mock lateinit var remoteAuthServiceFactory: RemoteAuthServiceFactory
  @Mock lateinit var unsplashClientFactory: UnsplashClientFactory
  @Mock lateinit var firebaseDatabaseHelper: FirebaseDatabaseHelper
  @Mock lateinit var databaseReference: DatabaseReference
  private lateinit var unsplashPictureEntityMapper: UnsplashPictureEntityMapper
  private lateinit var firebasePictureEntityMapper: FirebasePictureEntityMapper
  private lateinit var wallrDataRepository: WallrDataRepository
  private val randomString = randomUUID().toString()
  private val dummyInt = 500 // to force some error other than 403 or 404
  private val unableToResolveHostExceptionMessage = "Unable to resolve host " +
      "\"api.unsplash.com\": No address associated with hostname"
  private val purchasePreferenceName = "PURCHASE_PREF"
  private val premiumUserTag = "premium_user"

  @Before fun setup() {
    unsplashPictureEntityMapper = UnsplashPictureEntityMapper()
    firebasePictureEntityMapper = FirebasePictureEntityMapper()
    wallrDataRepository =
        WallrDataRepository(remoteAuthServiceFactory, unsplashClientFactory, sharedPrefs,
            unsplashPictureEntityMapper, firebaseDatabaseHelper, firebasePictureEntityMapper)
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

    val picture = wallrDataRepository.getSearchPictures(randomString)
        .test()
        .values()[0][0]

    assertTrue(searchPicturesModelList[0].id == picture.id)
    verify(unsplashClientFactory).getPicturesService(randomString)
    verifyNoMoreInteractions(unsplashClientFactory)
  }

}