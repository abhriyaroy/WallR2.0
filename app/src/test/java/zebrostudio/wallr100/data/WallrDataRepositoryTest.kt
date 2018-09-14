package zebrostudio.wallr100.data

import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.data.api.RemoteAuthServiceFactory
import org.mockito.Mock
import zebrostudio.wallr100.data.api.UnsplashClientFactory
import zebrostudio.wallr100.data.api.UrlMap
import zebrostudio.wallr100.data.datafactory.UnsplashPictureEntityModelFactory
import zebrostudio.wallr100.data.exception.InvalidPurchaseException
import zebrostudio.wallr100.data.exception.NoResultFoundException
import zebrostudio.wallr100.data.exception.UnableToVerifyPurchaseException
import zebrostudio.wallr100.data.mapper.PictureEntityMapper
import zebrostudio.wallr100.data.model.PurchaseAuthResponseEntity
import zebrostudio.wallr100.rules.TrampolineSchedulerRule
import java.util.Random

@RunWith(MockitoJUnitRunner::class)
class WallrDataRepositoryTest {

  @get:Rule var mTrampolineSchedulerRule = TrampolineSchedulerRule()

  @Mock lateinit var sharedPrefs: SharedPrefsHelper
  @Mock lateinit var remoteAuthServiceFactory: RemoteAuthServiceFactory
  @Mock lateinit var unsplashClientFactory: UnsplashClientFactory
  private lateinit var pictureEntityMapper: PictureEntityMapper
  private lateinit var wallrDataRepository: WallrDataRepository
  private val dummyString = java.util.UUID.randomUUID().toString()
  private val dummyInt = Random().nextInt() + 500 // to force some error other than 403 or 404

  @Before fun setup() {
    pictureEntityMapper = PictureEntityMapper()
    wallrDataRepository =
        WallrDataRepository(remoteAuthServiceFactory, unsplashClientFactory, sharedPrefs,
            pictureEntityMapper)
  }

  @Test fun `should return single on server success response`() {
    whenever(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(dummyString, dummyString, dummyString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("success", dummyInt, dummyString)))

    wallrDataRepository.authenticatePurchase(dummyString, dummyString, dummyString)
        .test()
        .assertComplete()
  }

  @Test fun `should return invalid purchase exception on server 403 error response`() {
    whenever(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(dummyString, dummyString, dummyString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("error", 403, dummyString)))

    wallrDataRepository.authenticatePurchase(dummyString, dummyString, dummyString)
        .test()
        .assertError(InvalidPurchaseException::class.java)
  }

  @Test fun `should return invalid purchase exception on server 404 error response`() {
    whenever(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(dummyString, dummyString, dummyString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity("error", 404, dummyString)))

    wallrDataRepository.authenticatePurchase(dummyString, dummyString, dummyString)
        .test()
        .assertError(InvalidPurchaseException::class.java)
  }

  @Test fun `should return unable to verify purchase exception on some error during call`() {
    whenever(remoteAuthServiceFactory.verifyPurchaseService(
        UrlMap.getFirebasePurchaseAuthEndpoint(dummyString, dummyString, dummyString)))
        .thenReturn(Single.just(PurchaseAuthResponseEntity(dummyString, dummyInt, dummyString)))

    wallrDataRepository.authenticatePurchase(dummyString, dummyString, dummyString)
        .test()
        .assertError(UnableToVerifyPurchaseException::class.java)
  }

  @Test fun `should return true after successful updating purchase operation`() {
    whenever(sharedPrefs.setBoolean(wallrDataRepository.purchasePreferenceName,
        wallrDataRepository.premiumUserTag, true)).thenReturn(true)

    assertEquals(true, wallrDataRepository.updateUserPurchaseStatus())
  }

  @Test fun `should return false after unsuccessful updating purchase operation`() {
    whenever(sharedPrefs.setBoolean(wallrDataRepository.purchasePreferenceName,
        wallrDataRepository.premiumUserTag, true)).thenReturn(false)

    assertEquals(false, wallrDataRepository.updateUserPurchaseStatus())
  }

  @Test fun `should return true after checking if user is premium user`() {
    whenever(sharedPrefs.getBoolean(wallrDataRepository.purchasePreferenceName,
        wallrDataRepository.premiumUserTag, false)).thenReturn(true)

    assertEquals(true, wallrDataRepository.isUserPremium())
  }

  @Test fun `should return false after checking if user is premium user`() {
    whenever(sharedPrefs.getBoolean(wallrDataRepository.purchasePreferenceName,
        wallrDataRepository.premiumUserTag, false)).thenReturn(false)

    assertEquals(false, wallrDataRepository.isUserPremium())
  }

  @Test fun `should return no result found exception on get pictures call`() {
    whenever(unsplashClientFactory.getPicturesService(dummyString)).thenReturn(
        Single.just(emptyList()))

    wallrDataRepository.getPictures(dummyString)
        .test()
        .assertError(NoResultFoundException::class.java)
  }

  @Test fun `should return mapped search pictures model list on get pictures call`() {
    val unsplashPicturesEntityList = mutableListOf(
        UnsplashPictureEntityModelFactory.getUnsplashPictureEntityModel())

    val searchPicturesModelList = pictureEntityMapper
        .mapFromEntity(unsplashPicturesEntityList)

    whenever(unsplashClientFactory.getPicturesService(dummyString)).thenReturn(
        Single.just(unsplashPicturesEntityList))

    val picture = wallrDataRepository.getPictures(dummyString)
        .test()
        .values()[0][0]

    assertEquals(searchPicturesModelList[0].id, picture.id)
    assertEquals(searchPicturesModelList[0].createdAt, picture.createdAt)
    assertEquals(searchPicturesModelList[0].imageWidth, picture.imageWidth)
    assertEquals(searchPicturesModelList[0].imageHeight, picture.imageHeight)
    assertEquals(searchPicturesModelList[0].paletteColor, picture.paletteColor)
    assertEquals(searchPicturesModelList[0].user.name, picture.user.name)
    assertEquals(searchPicturesModelList[0].user.profileImage.mediumImageUrl,
        picture.user.profileImage.mediumImageUrl)
    assertEquals(searchPicturesModelList[0].likes, picture.likes)
    assertEquals(searchPicturesModelList[0].likedByUser, picture.likedByUser)
    assertEquals(searchPicturesModelList[0].imageQualityUrls.largeImageLink,
        picture.imageQualityUrls.largeImageLink)
    assertEquals(searchPicturesModelList[0].imageQualityUrls.rawImageLink,
        picture.imageQualityUrls.rawImageLink)
    assertEquals(searchPicturesModelList[0].imageQualityUrls.regularImageLink,
        picture.imageQualityUrls.regularImageLink)
    assertEquals(searchPicturesModelList[0].imageQualityUrls.smallImageLink,
        picture.imageQualityUrls.smallImageLink)
    assertEquals(searchPicturesModelList[0].imageQualityUrls.thumbImageLink,
        picture.imageQualityUrls.thumbImageLink)
    assertEquals(searchPicturesModelList[0].categories, picture.categories)
  }

}