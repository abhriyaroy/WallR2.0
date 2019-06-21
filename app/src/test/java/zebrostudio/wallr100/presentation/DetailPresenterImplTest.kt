package zebrostudio.wallr100.presentation

import android.Manifest.permission
import android.app.Activity.RESULT_OK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import com.yalantis.ucrop.UCrop.REQUEST_CROP
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.permissions.PermissionsChecker
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig
import zebrostudio.wallr100.android.utils.ResourceUtils
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType.CRYSTALLIZED
import zebrostudio.wallr100.domain.model.collectionsimages.CollectionsImageType.EDITED
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.WALLPAPERS
import zebrostudio.wallr100.presentation.datafactory.ImagePresenterEntityFactory
import zebrostudio.wallr100.presentation.datafactory.SearchPicturesPresenterEntityFactory
import zebrostudio.wallr100.presentation.detail.images.ActionType.*
import zebrostudio.wallr100.presentation.detail.images.DetailContract
import zebrostudio.wallr100.presentation.detail.images.DetailPresenterImpl
import zebrostudio.wallr100.presentation.detail.images.mapper.ImageDownloadPresenterEntityMapper
import java.util.UUID.randomUUID

const val UNSUCCESSFUL_PURCHASE_CODE = 0

@RunWith(MockitoJUnitRunner::class)
class DetailPresenterImplTest {

  @Mock
  private lateinit var imageOptionsUseCase: ImageOptionsUseCase
  @Mock
  private lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase
  @Mock
  private lateinit var detailView: DetailContract.DetailView
  @Mock
  private lateinit var wallpaperSetter: WallpaperSetter
  @Mock
  private lateinit var mockBitmap: Bitmap
  @Mock
  private lateinit var resourceUtils: ResourceUtils
  @Mock
  private lateinit var postExecutionThread: PostExecutionThread
  @Mock
  private lateinit var permissionsChecker: PermissionsChecker
  @Mock
  private lateinit var mockUri: Uri
  @Mock
  private lateinit var mockDestinationUri: Uri
  private lateinit var detailPresenterImpl: DetailPresenterImpl
  private lateinit var testScopeProvider: TestLifecycleScopeProvider
  private val downloadProgressCompletedValue: Long = 100
  private val downloadProgressCompleteUpTo99: Long = 99
  private val downloadProgressCompleteUpTo98: Long = 98
  private val indefiniteLoaderMessage = "Finalizing wallpaper..."
  private var randomString = randomUUID().toString()
  private val wallrDownloadLink = "http://bit.ly/download_wallr"
  private lateinit var imageDownloadPresenterEntityMapper: ImageDownloadPresenterEntityMapper

  @Before
  fun setup() {
    imageDownloadPresenterEntityMapper =
        ImageDownloadPresenterEntityMapper()
    detailPresenterImpl =
        DetailPresenterImpl(resourceUtils,
          imageOptionsUseCase, userPremiumStatusUseCase,
          wallpaperSetter, postExecutionThread, imageDownloadPresenterEntityMapper,
          permissionsChecker)
    detailPresenterImpl.attachView(detailView)

    testScopeProvider = TestLifecycleScopeProvider.createInitial(
      TestLifecycleScopeProvider.TestLifecycle.STARTED)
    `when`(detailView.getScope()).thenReturn(testScopeProvider)
    stubPostExecutionThreadReturnsIoScheduler()
  }

  @Test
  fun `should show search image details on setImageType call success with type as search`() {
    val searchImagePresenterEntity =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(detailView.getSearchImageDetails()).thenReturn(searchImagePresenterEntity)

    detailPresenterImpl.setImageType(SEARCH.ordinal)

    assertEquals(SEARCH, detailPresenterImpl.imageType)
    verify(detailView).getSearchImageDetails()
    verify(detailView).showAuthorDetails(searchImagePresenterEntity.userPresenterEntity.name,
      searchImagePresenterEntity.userPresenterEntity.profileImageLink)
    verify(detailView).showImage(
      searchImagePresenterEntity.imageQualityUrlPresenterEntity.smallImageLink,
      searchImagePresenterEntity.imageQualityUrlPresenterEntity.largeImageLink)
  }

  @Test
  fun `should show wallpaper image details on setImageType call success with type as wallpaper`() {
    val imagePresenterEntity = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(detailView.getWallpaperImageDetails()).thenReturn(imagePresenterEntity)

    detailPresenterImpl.setImageType(WALLPAPERS.ordinal)

    assertEquals(WALLPAPERS, detailPresenterImpl.imageType)
    verify(detailView).getWallpaperImageDetails()
    verify(detailView).showAuthorDetails(imagePresenterEntity.author.name,
      imagePresenterEntity.author.profileImageLink)
    verify(detailView).showImage(imagePresenterEntity.imageLink.thumb,
      imagePresenterEntity.imageLink.large)
  }

  @Test
  fun `should show image load error toast on handleHighQualityImageLoadFailed call success`() {
    detailPresenterImpl.handleHighQualityImageLoadFailed()

    verify(detailView).showImageLoadError()
  }

  @Test
  fun `should show no internet error on handleShareClicked call failure due to no internet availability`() {
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleShareClick()

    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetToShareError()
  }

  @Test
  fun `should redirect to pro when handleShareClicked call failure due to non pro user`() {
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    detailPresenterImpl.handleShareClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(detailView).internetAvailability()
    verify(detailView).redirectToBuyPro(SHARE.ordinal)
  }

  @Test
  fun `should show generic error on handleShareClicked call failure of type search`() {
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(imageOptionsUseCase.getImageShareableLinkSingle(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Single.error(Exception()))

    detailPresenterImpl.handleShareClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).getImageShareableLinkSingle(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(detailView).internetAvailability()
    verify(detailView).getScope()
    verify(detailView).hideWaitLoader()
    verify(detailView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should share link on handleShareClicked call success of type search`() {
    val shareIntentType = "text/plain"
    val shareIntentMessage = "share intent message"
    val message = "$shareIntentMessage $wallrDownloadLink \n\n Image link - $randomString"
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(resourceUtils.getStringResource(R.string.share_intent_message))
        .thenReturn(shareIntentMessage)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(imageOptionsUseCase.getImageShareableLinkSingle(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Single.just(randomString))

    detailPresenterImpl.handleShareClick()

    verify(resourceUtils).getStringResource(R.string.share_intent_message)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).getImageShareableLinkSingle(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(detailView).internetAvailability()
    verify(detailView).getScope()
    verify(detailView).hideWaitLoader()
    verify(detailView).shareLink(message, shareIntentType)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error on handleShareClicked call failure of type wallpaper`() {
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage =
        ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(imageOptionsUseCase.getImageShareableLinkSingle(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Single.error(Exception()))

    detailPresenterImpl.handleShareClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).getImageShareableLinkSingle(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(detailView).internetAvailability()
    verify(detailView).getScope()
    verify(detailView).hideWaitLoader()
    verify(detailView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should share link on handleShareClicked call success of type wallpaper`() {
    val shareIntentType = "text/plain"
    val shareIntentMessage = "share intent message"
    val message = "$shareIntentMessage $wallrDownloadLink \n\n Image link - $randomString"
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage =
        ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(resourceUtils.getStringResource(R.string.share_intent_message))
        .thenReturn(shareIntentMessage)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(imageOptionsUseCase.getImageShareableLinkSingle(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Single.just(randomString))

    detailPresenterImpl.handleShareClick()

    verify(resourceUtils).getStringResource(R.string.share_intent_message)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).getImageShareableLinkSingle(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(detailView).internetAvailability()
    verify(detailView).getScope()
    verify(detailView).hideWaitLoader()
    verify(detailView).shareLink(message, shareIntentType)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show no internet error on handleViewResult call failure of type share when premium purchase is successful but internet is not available`() {
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleViewResult(SHARE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetToShareError()
  }

  @Test
  fun `should show purchase unsuccessful message on handleViewResult call failure of type share due to unsuccessful purchase`() {
    detailPresenterImpl.handleViewResult(SHARE.ordinal, UNSUCCESSFUL_PURCHASE_CODE)

    verify(detailView).showUnsuccessfulPurchaseError()
  }

  @Test
  fun `should show generic error on handleViewResult call failure of type share with image type search`() {
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(imageOptionsUseCase.getImageShareableLinkSingle(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Single.error(Exception()))

    detailPresenterImpl.handleViewResult(SHARE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).getImageShareableLinkSingle(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(detailView).internetAvailability()
    verify(detailView).getScope()
    verify(detailView).hideWaitLoader()
    verify(detailView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should share link on handleViewResult call success of type share with image type search`() {
    val shareIntentType = "text/plain"
    val shareIntentMessage = "share intent message"
    val message = "$shareIntentMessage $wallrDownloadLink \n\n Image link - $randomString"
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(resourceUtils.getStringResource(R.string.share_intent_message))
        .thenReturn(shareIntentMessage)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(imageOptionsUseCase.getImageShareableLinkSingle(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Single.just(randomString))

    detailPresenterImpl.handleViewResult(SHARE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(resourceUtils).getStringResource(R.string.share_intent_message)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).getImageShareableLinkSingle(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(detailView).internetAvailability()
    verify(detailView).getScope()
    verify(detailView).hideWaitLoader()
    verify(detailView).shareLink(message, shareIntentType)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error on handleViewResult call failure of type share with image type wallpaper`() {
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage =
        ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(imageOptionsUseCase.getImageShareableLinkSingle(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Single.error(Exception()))

    detailPresenterImpl.handleViewResult(SHARE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).getImageShareableLinkSingle(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(detailView).internetAvailability()
    verify(detailView).getScope()
    verify(detailView).hideWaitLoader()
    verify(detailView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should share link on handleViewResult call success of type share with image type as wallpaper`() {
    val shareIntentType = "text/plain"
    val shareIntentMessage = "share intent message"
    val message = "$shareIntentMessage $wallrDownloadLink \n\n Image link - $randomString"
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage =
        ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(resourceUtils.getStringResource(R.string.share_intent_message))
        .thenReturn(shareIntentMessage)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(imageOptionsUseCase.getImageShareableLinkSingle(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Single.just(randomString))

    detailPresenterImpl.handleViewResult(SHARE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(resourceUtils).getStringResource(R.string.share_intent_message)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).getImageShareableLinkSingle(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(detailView).internetAvailability()
    verify(detailView).getScope()
    verify(detailView).hideWaitLoader()
    verify(detailView).shareLink(message, shareIntentType)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should request storage permission on handleQuickSetClicked call failure due to missing read storage permission`() {
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleQuickSetClick()

    verify(permissionsChecker).isReadPermissionAvailable()
    verify(detailView).requestStoragePermission(QUICK_SET)
  }

  @Test
  fun `should request storage permission on handleQuickSetClicked call failure due to missing write storage permission`() {
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(true)
    `when`(permissionsChecker.isWritePermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleQuickSetClick()

    verify(permissionsChecker).isReadPermissionAvailable()
    verify(permissionsChecker).isWritePermissionAvailable()
    verify(detailView).requestStoragePermission(QUICK_SET)
  }

  @Test
  fun `should show no internet error on handleQuickSetClicked call failure due to no internet availability`() {
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleQuickSetClick()

    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
  }

  @Test
  fun `should show image download progress on handleQuickSetClicked call success of type search with progress value 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handleQuickSetClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, true)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show indefinite loader wit animation on handleQuickSetClicked call success of type search with progress value 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(indefiniteLoaderMessage)

    detailPresenterImpl.handleQuickSetClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    assertEquals(detailPresenterImpl.isImageOperationInProgress, true)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(indefiniteLoaderMessage)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set wallpaper on handleQuickSetClicked call success of type search with progress value 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
        it.onComplete()
      })
    `when`(wallpaperSetter.setWallpaper(mockBitmap)).thenReturn(true)
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(indefiniteLoaderMessage)

    detailPresenterImpl.handleQuickSetClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    assertEquals(detailPresenterImpl.isImageOperationInProgress, false)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).showIndefiniteLoader(indefiniteLoaderMessage)
    verify(detailView).showWallpaperSetSuccessMessage()
    verify(detailView).hideScreenBlur()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show image download progress on handleQuickSetClicked call success of type wallpaper with progress value 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handleQuickSetClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, true)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show indefinite loader wit animation on handleQuickSetClicked call success of type wallpaper with progress value 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(indefiniteLoaderMessage)

    detailPresenterImpl.handleQuickSetClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    assertEquals(detailPresenterImpl.isImageOperationInProgress, true)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(indefiniteLoaderMessage)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set wallpaper on handleQuickSetClicked call success of type wallpaper with progress value 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
        it.onComplete()
      })
    `when`(wallpaperSetter.setWallpaper(mockBitmap)).thenReturn(true)
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(indefiniteLoaderMessage)

    detailPresenterImpl.handleQuickSetClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    assertEquals(detailPresenterImpl.isImageOperationInProgress, false)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).showIndefiniteLoader(indefiniteLoaderMessage)
    verify(detailView).showWallpaperSetSuccessMessage()
    verify(detailView).hideScreenBlur()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show permission required message on handlePermissionRequestResult call failure of type quick set due to permission not granted`() {
    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
      arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
      intArrayOf(PackageManager.PERMISSION_DENIED))

    verify(detailView).showPermissionRequiredMessage()
    verify(detailView).showPermissionRequiredRationale()
  }

  @Test
  fun `should show no internet error on handlePermissionRequestResult call failure of type quick set due to no internet availability`() {
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
      arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
  }

  @Test
  fun `should show progress on handlePermissionRequestResult call success of type quick set when progress value is 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
      arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertEquals(detailPresenterImpl.isDownloadInProgress, true)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show finalizing wallpaper message on handlePermissionRequestResult call success of type quick set when progress value is 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(indefiniteLoaderMessage)

    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
      arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    assertEquals(detailPresenterImpl.isImageOperationInProgress, true)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(indefiniteLoaderMessage)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set wallpaper successfully on handlePermissionRequestResult call success of type quick set when progress value is 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.just(imageDownloadModel))
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(wallpaperSetter.setWallpaper(mockBitmap)).thenReturn(true)
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(indefiniteLoaderMessage)

    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
      arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    assertEquals(detailPresenterImpl.isImageOperationInProgress, false)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).showIndefiniteLoader(indefiniteLoaderMessage)
    verify(detailView).showWallpaperSetSuccessMessage()
    verify(detailView).hideScreenBlur()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show set wallpaper error message after handlePermissionRequestResult call failure of type quick set due to wallpaper setter returning false`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.just(imageDownloadModel))
    `when`(wallpaperSetter.setWallpaper(mockBitmap)).thenReturn(false)
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(randomString)

    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
      arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertEquals(detailPresenterImpl.isImageOperationInProgress, false)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).showWallpaperSetErrorMessage()
    verify(detailView).hideScreenBlur()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should cancel download with message on handleBackButtonClick call success while download in progress`() {
    detailPresenterImpl.isDownloadInProgress = true

    detailPresenterImpl.handleBackButtonClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    verify(imageOptionsUseCase).cancelFetchImageOperation()
    verify(detailView).hideScreenBlur()
    verify(detailView).showDownloadWallpaperCancelledMessage()
  }

  @Test
  fun `should show wait message on handleBackButtonClick call success while image operation is in progress`() {
    detailPresenterImpl.isImageOperationInProgress = true

    detailPresenterImpl.handleBackButtonClick()

    verify(detailView).showWallpaperOperationInProgressWaitMessage()
  }

  @Test
  fun `should exit activity on handleBackButtonClick call success and clear cache is successful`() {
    detailPresenterImpl.isDownloadInProgress = false
    detailPresenterImpl.isImageOperationInProgress = false
    `when`(imageOptionsUseCase.clearCachesCompletable()).thenReturn(Completable.complete())

    detailPresenterImpl.handleBackButtonClick()

    verify(imageOptionsUseCase).clearCachesCompletable()
    verify(detailView).getScope()
    verify(detailView).exitView()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show progress on handleEditSetClick call success of type edit set when progress value is 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handleEditSetClick()

    assertEquals(true, detailPresenterImpl.isDownloadInProgress)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show indefinite loader on handleEditSetClick call success of type edit set when progress value is 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = WALLPAPERS
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.detail_activity_editing_tool_message))
        .thenReturn(randomString)

    detailPresenterImpl.handleEditSetClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    assertEquals(detailPresenterImpl.isImageOperationInProgress, true)
    verify(resourceUtils).getStringResource(R.string.detail_activity_editing_tool_message)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should start cropping activity on handleEditSetClick call success of type edit set when progress value is 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, null)
    val height = 1
    val width = 2
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(imageOptionsUseCase.getCroppingSourceUri()).thenReturn(Single.just(mockUri))
    `when`(imageOptionsUseCase.getCroppingDestinationUri()).thenReturn(
      Single.just(mockDestinationUri))
    `when`(wallpaperSetter.getDesiredMinimumWidth()).thenReturn(width)
    `when`(wallpaperSetter.getDesiredMinimumHeight()).thenReturn(height)

    detailPresenterImpl.handleEditSetClick()

    verify(imageOptionsUseCase).getCroppingSourceUri()
    verify(imageOptionsUseCase).getCroppingDestinationUri()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(wallpaperSetter).getDesiredMinimumWidth()
    verify(wallpaperSetter).getDesiredMinimumHeight()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).startCroppingActivity(mockUri,
      mockDestinationUri,
      width,
      height)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should request storage permission on handleEditSetClick call failure due to missing read storage permission`() {
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleEditSetClick()

    verify(permissionsChecker).isReadPermissionAvailable()
    verify(detailView).internetAvailability()
    verify(detailView).requestStoragePermission(EDIT_SET)
  }

  @Test
  fun `should request storage permission on handleEditSetClick call failure due to missing write storage permission`() {
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(true)
    `when`(permissionsChecker.isWritePermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleEditSetClick()

    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).requestStoragePermission(EDIT_SET)
  }

  @Test
  fun `should show permission required message on handlePermissionRequestResult call failure of type edit set due to permission not granted`() {
    detailPresenterImpl.handlePermissionRequestResult(EDIT_SET.ordinal,
      arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
      intArrayOf(PackageManager.PERMISSION_DENIED))

    verify(detailView).showPermissionRequiredMessage()
    verify(detailView).showPermissionRequiredRationale()
  }

  @Test
  fun `should show progress on handlePermissionRequestResult call success of type edit set when progress value is 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handlePermissionRequestResult(EDIT_SET.ordinal,
      arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertEquals(detailPresenterImpl.isDownloadInProgress, true)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show indefinite loader on handlePermissionRequestResult call success of type edit set when progress value is 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = WALLPAPERS
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.detail_activity_editing_tool_message))
        .thenReturn(randomString)

    detailPresenterImpl.handlePermissionRequestResult(EDIT_SET.ordinal,
      arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    assertEquals(detailPresenterImpl.isImageOperationInProgress, true)
    verify(resourceUtils).getStringResource(R.string.detail_activity_editing_tool_message)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should start cropping activity on handlePermissionRequestResult call success of type edit set when progress value is 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, null)
    val height = 1
    val width = 2
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(imageOptionsUseCase.getCroppingSourceUri()).thenReturn(Single.just(mockUri))
    `when`(imageOptionsUseCase.getCroppingDestinationUri()).thenReturn(
      Single.just(mockDestinationUri))
    `when`(wallpaperSetter.getDesiredMinimumWidth()).thenReturn(width)
    `when`(wallpaperSetter.getDesiredMinimumHeight()).thenReturn(height)

    detailPresenterImpl.handlePermissionRequestResult(EDIT_SET.ordinal,
      arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(imageOptionsUseCase).getCroppingSourceUri()
    verify(imageOptionsUseCase).getCroppingDestinationUri()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(wallpaperSetter).getDesiredMinimumWidth()
    verify(wallpaperSetter).getDesiredMinimumHeight()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).startCroppingActivity(mockUri,
      mockDestinationUri,
      width,
      height)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show error on handleViewResult call failure due to null crop uri`() {
    `when`(detailView.getUriFromResultIntent()).thenReturn(null)
    `when`(imageOptionsUseCase.getBitmapFromUriSingle(null)).thenReturn(
      Single.error(NullPointerException()))
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(randomString)

    detailPresenterImpl.handleViewResult(REQUEST_CROP, RESULT_OK)

    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).getBitmapFromUriSingle(null)
    verify(detailView).getUriFromResultIntent()
    verify(detailView).getScope()
    verify(detailView).blurScreen()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).hideScreenBlur()
    verify(detailView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set wallpaper and show success message on handleViewResult call success with request code crop`() {
    `when`(detailView.getUriFromResultIntent()).thenReturn(mockUri)
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(randomString)
    `when`(imageOptionsUseCase.getBitmapFromUriSingle(mockUri)).thenReturn(Single.just(mockBitmap))
    `when`(wallpaperSetter.setWallpaper(mockBitmap)).thenReturn(true)

    detailPresenterImpl.handleViewResult(REQUEST_CROP, RESULT_OK)

    assertEquals(false, detailPresenterImpl.isImageOperationInProgress)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).getBitmapFromUriSingle(mockUri)
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    verify(detailView).getUriFromResultIntent()
    verify(detailView).blurScreen()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).getScope()
    verify(detailView).showImage(mockBitmap)
    verify(detailView).showWallpaperSetSuccessMessage()
    verify(detailView).hideScreenBlur()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show wallpaper setting error message on handleViewResult call failure with request code crop due to wallpaper setter error`() {
    `when`(detailView.getUriFromResultIntent()).thenReturn(mockUri)
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(randomString)
    `when`(imageOptionsUseCase.getBitmapFromUriSingle(mockUri)).thenReturn(Single.just(mockBitmap))
    `when`(wallpaperSetter.setWallpaper(mockBitmap)).thenReturn(false)

    detailPresenterImpl.handleViewResult(REQUEST_CROP, RESULT_OK)

    assertEquals(false, detailPresenterImpl.isImageOperationInProgress)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).getBitmapFromUriSingle(mockUri)
    verify(wallpaperSetter).setWallpaper(mockBitmap)
    verify(detailView).getUriFromResultIntent()
    verify(detailView).blurScreen()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).getScope()
    verify(detailView).showWallpaperSetErrorMessage()
    verify(detailView).hideScreenBlur()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error message on handleViewResult call failure with request code crop due to getBitmapFromUriSingle error`() {
    `when`(detailView.getUriFromResultIntent()).thenReturn(mockUri)
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(randomString)
    `when`(imageOptionsUseCase.getBitmapFromUriSingle(mockUri))
        .thenReturn(Single.error(Exception()))

    detailPresenterImpl.handleViewResult(REQUEST_CROP, RESULT_OK)

    assertEquals(false, detailPresenterImpl.isImageOperationInProgress)
    verify(imageOptionsUseCase).getBitmapFromUriSingle(mockUri)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(detailView).getUriFromResultIntent()
    verify(detailView).blurScreen()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).getScope()
    verify(detailView).showGenericErrorMessage()
    verify(detailView).hideScreenBlur()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error message when crop activity results to failure`() {
    `when`(detailView.getUriFromResultIntent()).thenReturn(mockUri)
    `when`(resourceUtils.getStringResource(R.string.finalizing_wallpaper_messsage))
        .thenReturn(randomString)
    `when`(imageOptionsUseCase.getBitmapFromUriSingle(mockUri))
        .thenReturn(Single.error(Exception()))

    detailPresenterImpl.handleViewResult(REQUEST_CROP, RESULT_OK)

    assertEquals(false, detailPresenterImpl.isImageOperationInProgress)
    verify(resourceUtils).getStringResource(R.string.finalizing_wallpaper_messsage)
    verify(imageOptionsUseCase).getBitmapFromUriSingle(mockUri)
    verify(detailView).getUriFromResultIntent()
    verify(detailView).blurScreen()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).getScope()
    verify(detailView).showGenericErrorMessage()
    verify(detailView).hideScreenBlur()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should redirect to buy pro on handleDownloadClick call failure due to non pro user`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(detailView).redirectToBuyPro(DOWNLOAD.ordinal)
  }

  @Test
  fun `should request storage permission on handleDownloadClick call failure due to read storage permission not available`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(permissionsChecker).isReadPermissionAvailable()
    verify(detailView).requestStoragePermission(DOWNLOAD)
  }

  @Test
  fun `should request storage permission on handleDownloadClick call failure due to write storage permission not available`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(true)
    `when`(permissionsChecker.isWritePermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).requestStoragePermission(DOWNLOAD)
  }

  @Test
  fun `should show no internet error message on handleDownloadClick call failure due to internet not available`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
  }

  @Test
  fun `should show download dialog with crystallized search image option on handleDownloadClick call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = true

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showSearchTypeDownloadDialog(true)
  }

  @Test
  fun `should show download dialog without crystallized search image option on handleDownloadClick call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = false

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showSearchTypeDownloadDialog(false)
  }

  @Test
  fun `should show download dialog with crystallized wallpaper image option on handleDownloadClick call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.imageHasBeenCrystallized = true

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showWallpaperTypeDownloadDialog(true)
  }

  @Test
  fun `should show download dialog without crystallized wallpaper image option on handleDownloadClick call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.imageHasBeenCrystallized = false

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showWallpaperTypeDownloadDialog(false)
  }

  @Test
  fun `should show permission required message on handlePermissionRequestResult call failure of type download due to permission not granted`() {
    detailPresenterImpl.handlePermissionRequestResult(DOWNLOAD.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_DENIED))

    verify(detailView).showPermissionRequiredMessage()
    verify(detailView).showPermissionRequiredRationale()
  }

  @Test
  fun `should request storage permission on handlePermissionRequestResult call failure of type download due to missing read storage permission`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handlePermissionRequestResult(DOWNLOAD.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(permissionsChecker).isReadPermissionAvailable()
    verify(detailView).requestStoragePermission(DOWNLOAD)
  }

  @Test
  fun `should request storage permission on handlePermissionRequestResult call failure of type download due to missing write storage permission`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(true)
    `when`(permissionsChecker.isWritePermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handlePermissionRequestResult(DOWNLOAD.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).requestStoragePermission(DOWNLOAD)
  }

  @Test
  fun `should show no internet error message on handlePermissionRequestResult call failure of type download due to internet not available`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handlePermissionRequestResult(DOWNLOAD.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
  }

  @Test
  fun `should show download dialog with crystallized search image option on handlePermissionRequestResult call success of type download`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = true

    detailPresenterImpl.handlePermissionRequestResult(DOWNLOAD.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showSearchTypeDownloadDialog(true)
  }

  @Test
  fun `should show download dialog without crystallized search image option on handlePermissionRequestResult call success of type download`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = false

    detailPresenterImpl.handlePermissionRequestResult(DOWNLOAD.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showSearchTypeDownloadDialog(false)
  }

  @Test
  fun `should show download dialog with crystallized wallpaper image option on handlePermissionRequestResult call success of type download`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.imageHasBeenCrystallized = true

    detailPresenterImpl.handlePermissionRequestResult(DOWNLOAD.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showWallpaperTypeDownloadDialog(true)
  }

  @Test
  fun `should show download dialog without crystallized wallpaper image option on handlePermissionRequestResult call success of type download`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.imageHasBeenCrystallized = false

    detailPresenterImpl.handlePermissionRequestResult(DOWNLOAD.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showWallpaperTypeDownloadDialog(false)
  }

  @Test
  fun `should show purchase unsuccessful message on handleViewResult of download type call failure`() {
    detailPresenterImpl.handleViewResult(DOWNLOAD.ordinal, UNSUCCESSFUL_PURCHASE_CODE)

    verify(detailView).showUnsuccessfulPurchaseError()
  }

  @Test
  fun `should request storage permission on handleViewResult call failure of type download due to missing read storage permission`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleViewResult(DOWNLOAD.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(permissionsChecker).isReadPermissionAvailable()
    verify(detailView).requestStoragePermission(DOWNLOAD)
  }

  @Test
  fun `should request storage permission on handleViewResult call failure of type download due to missing write storage permission`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(true)
    `when`(permissionsChecker.isWritePermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleViewResult(DOWNLOAD.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).requestStoragePermission(DOWNLOAD)
  }

  @Test
  fun `should show no internet error message on handleViewResult call failure of type download due to internet not available`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleViewResult(DOWNLOAD.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
  }

  @Test
  fun `should show download dialog with crystallized search image option on handleViewResult call success of type download`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = true

    detailPresenterImpl.handleViewResult(DOWNLOAD.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showSearchTypeDownloadDialog(true)
  }

  @Test
  fun `should show download dialog without crystallized search image option on handleViewResult call success of type download`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = false

    detailPresenterImpl.handleViewResult(DOWNLOAD.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showSearchTypeDownloadDialog(false)
  }

  @Test
  fun `should show download dialog with crystallized wallpaper image option on handleViewResult call success of type download`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.imageHasBeenCrystallized = true

    detailPresenterImpl.handleViewResult(DOWNLOAD.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showWallpaperTypeDownloadDialog(true)
  }

  @Test
  fun `should show download dialog without crystallized wallpaper image option on handleViewResult call success of type download`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.imageHasBeenCrystallized = false

    detailPresenterImpl.handleViewResult(DOWNLOAD.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showWallpaperTypeDownloadDialog(false)
  }

  @Test
  fun `should show download complete message on handleDownloadQualitySelectionEvent call success of search image of super high quality`() {
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.rawImageLink
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(Completable.complete())

    detailPresenterImpl.handleDownloadQualitySelectionEvent(SEARCH, 0)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error message on handleDownloadQualitySelectionEvent call success of search image of super high quality`() {
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.rawImageLink
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(
      Completable.error(Exception()))

    detailPresenterImpl.handleDownloadQualitySelectionEvent(SEARCH, 0)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show download complete message on handleDownloadQualitySelectionEvent call success of search image of high quality`() {
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(Completable.complete())

    detailPresenterImpl.handleDownloadQualitySelectionEvent(SEARCH, 1)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show download complete message on handleDownloadQualitySelectionEvent call success of search image of medium quality`() {
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.regularImageLink
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(Completable.complete())

    detailPresenterImpl.handleDownloadQualitySelectionEvent(SEARCH, 2)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show download complete message on handleDownloadQualitySelectionEvent call success of search image of low quality`() {
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.thumbImageLink
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(Completable.complete())

    detailPresenterImpl.handleDownloadQualitySelectionEvent(SEARCH, 3)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show download complete message on handleDownloadQualitySelectionEvent call success of search image of super low quality`() {
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.smallImageLink
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(Completable.complete())

    detailPresenterImpl.handleDownloadQualitySelectionEvent(SEARCH, 4)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show download complete message on handleDownloadQualitySelectionEvent call success of wallpaper image of super high quality`() {
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    val link = detailPresenterImpl.wallpaperImage.imageLink.raw
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(Completable.complete())

    detailPresenterImpl.handleDownloadQualitySelectionEvent(WALLPAPERS, 0)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error message on handleDownloadQualitySelectionEvent call success of wallpaper image of super high quality`() {
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    val link = detailPresenterImpl.wallpaperImage.imageLink.raw
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(
      Completable.error(Exception()))

    detailPresenterImpl.handleDownloadQualitySelectionEvent(WALLPAPERS, 0)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showGenericErrorMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show download complete message on handleDownloadQualitySelectionEvent call success of wallpaper image of high quality`() {
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    val link = detailPresenterImpl.wallpaperImage.imageLink.large
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(Completable.complete())

    detailPresenterImpl.handleDownloadQualitySelectionEvent(WALLPAPERS, 1)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show download complete message on handleDownloadQualitySelectionEvent call success of wallpaper image of medium quality`() {
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    val link = detailPresenterImpl.wallpaperImage.imageLink.medium
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(Completable.complete())

    detailPresenterImpl.handleDownloadQualitySelectionEvent(WALLPAPERS, 2)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show download complete message on handleDownloadQualitySelectionEvent call success of wallpaper image of low quality`() {
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    val link = detailPresenterImpl.wallpaperImage.imageLink.thumb
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(Completable.complete())

    detailPresenterImpl.handleDownloadQualitySelectionEvent(WALLPAPERS, 3)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show download complete message on handleDownloadQualitySelectionEvent call success of wallpaper image of super low quality`() {
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    val link = detailPresenterImpl.wallpaperImage.imageLink.small
    `when`(imageOptionsUseCase.isDownloadInProgress(link)).thenReturn(false)
    `when`(imageOptionsUseCase.downloadImageCompletable(link)).thenReturn(Completable.complete())

    detailPresenterImpl.handleDownloadQualitySelectionEvent(WALLPAPERS, 4)

    verify(imageOptionsUseCase).isDownloadInProgress(link)
    verify(imageOptionsUseCase).downloadImageCompletable(link)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should redirect to pro when handleCrystallizeClicked call failure due to non pro user`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    detailPresenterImpl.handleCrystallizeClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(detailView).redirectToBuyPro(CRYSTALLIZE.ordinal)
  }

  @Test
  fun `should request storage permission on handleCrystallizeClick call failure on missing read storage permission`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleCrystallizeClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(permissionsChecker).isReadPermissionAvailable()
    verify(detailView).requestStoragePermission(CRYSTALLIZE)
  }

  @Test
  fun `should request storage permission on handleCrystallizeClick call failure on missing write storage permission`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(true)
    `when`(permissionsChecker.isWritePermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleCrystallizeClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).requestStoragePermission(CRYSTALLIZE)
  }

  @Test
  fun `should show no internet error on handleCrystallizeClick call failure due to no internet availability`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleCrystallizeClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
  }

  @Test
  fun `should show crystallize description dialog on handleCrystallizeClick call success and crystallize description not shown yet`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(false)

    detailPresenterImpl.handleCrystallizeClick()

    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showCrystallizeDescriptionDialog()
    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(userPremiumStatusUseCase).isUserPremium()
  }

  @Test
  fun `should set bitmap to image view on handleCrystallize call success of search type`() {
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
      Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
      Single.just(Pair(true, mockBitmap)))

    detailPresenterImpl.handleCrystallizeClick()

    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showImage(mockBitmap)
    verify(detailView).hideScreenBlur()
    verify(detailView).showCrystallizeSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should show permission required message on handlePermissionRequestResult with crystallize request code type call failure`() {
    detailPresenterImpl.handlePermissionRequestResult(CRYSTALLIZE.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_DENIED))

    verify(detailView).showPermissionRequiredMessage()
    verify(detailView).showPermissionRequiredRationale()
  }

  @Test
  fun `should show no internet error on handlePermissionRequestResult with crystallize request code call failure due to no internet availability`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handlePermissionRequestResult(CRYSTALLIZE.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
  }

  @Test
  fun `should show crystallize description dialog on handlePermissionRequestResult with crystallize request code call success and crystallize description not shown yet`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(false)

    detailPresenterImpl.handlePermissionRequestResult(CRYSTALLIZE.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showCrystallizeDescriptionDialog()
    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(userPremiumStatusUseCase).isUserPremium()
  }

  @Test
  fun `should set bitmap to image view on handlePermissionRequestResult with crystallize request code call success of search type`() {
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
      Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
      Single.just(Pair(true, mockBitmap)))

    detailPresenterImpl.handlePermissionRequestResult(CRYSTALLIZE.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showImage(mockBitmap)
    verify(detailView).hideScreenBlur()
    verify(detailView).showCrystallizeSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should show generic error message on handlePermissionRequestResult with crystallize request code call failure of search type`() {
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
      Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
      Single.error(Exception()))

    detailPresenterImpl.handlePermissionRequestResult(CRYSTALLIZE.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showGenericErrorMessage()
    verify(detailView).hideScreenBlur()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should set bitmap to image view on handlePermissionRequestResult with crystallize request code call success of wallpaper type`() {
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    val link = detailPresenterImpl.wallpaperImage.imageLink.large
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
      Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
      Single.just(Pair(true, mockBitmap)))

    detailPresenterImpl.handlePermissionRequestResult(CRYSTALLIZE.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showImage(mockBitmap)
    verify(detailView).hideScreenBlur()
    verify(detailView).showCrystallizeSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should show generic error message on handlePermissionRequestResult with crystallize request code call failure of wallpaper type`() {
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    val link = detailPresenterImpl.wallpaperImage.imageLink.large
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
      Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
      Single.error(Exception()))

    detailPresenterImpl.handlePermissionRequestResult(CRYSTALLIZE.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showGenericErrorMessage()
    verify(detailView).hideScreenBlur()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should show purchase unsuccessful message on handleViewResult of crystallize type call failure`() {
    detailPresenterImpl.handleViewResult(CRYSTALLIZE.ordinal, UNSUCCESSFUL_PURCHASE_CODE)

    verify(detailView).showUnsuccessfulPurchaseError()
  }

  @Test
  fun `should show no internet error on handleViewResult of crystallize type call failure due to no internet availability`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleViewResult(CRYSTALLIZE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
  }

  @Test
  fun `should show crystallize description dialog on handleViewResult of crystallize type call success and crystallize description not shown yet`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(false)

    detailPresenterImpl.handleViewResult(CRYSTALLIZE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showCrystallizeDescriptionDialog()
    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(userPremiumStatusUseCase).isUserPremium()
  }

  @Test
  fun `should set bitmap to image view on handleViewResult with crystallize request code call success of search type`() {
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
      Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
      Single.just(Pair(true, mockBitmap)))

    detailPresenterImpl.handleViewResult(CRYSTALLIZE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showImage(mockBitmap)
    verify(detailView).hideScreenBlur()
    verify(detailView).showCrystallizeSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should show generic error message on handleViewResult with crystallize request code call failure of search type`() {
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
      Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
      Single.error(Exception()))

    detailPresenterImpl.handleViewResult(CRYSTALLIZE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showGenericErrorMessage()
    verify(detailView).hideScreenBlur()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should set bitmap to image view on handleViewResult with crystallize request code call success of wallpaper type`() {
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    val link = detailPresenterImpl.wallpaperImage.imageLink.large
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
      Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
      Single.just(Pair(true, mockBitmap)))

    detailPresenterImpl.handleViewResult(CRYSTALLIZE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showImage(mockBitmap)
    verify(detailView).hideScreenBlur()
    verify(detailView).showCrystallizeSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should show generic error message on handleViewResult with crystallize request code call failure of wallpaper type`() {
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    val link = detailPresenterImpl.wallpaperImage.imageLink.large
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
      Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
      Single.error(Exception()))

    detailPresenterImpl.handleViewResult(CRYSTALLIZE.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showGenericErrorMessage()
    verify(detailView).hideScreenBlur()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should redirect to pro when handleAddToCollectionClick call failure due to non pro user`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    detailPresenterImpl.handleAddToCollectionClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(detailView).redirectToBuyPro(ADD_TO_COLLECTION.ordinal)
  }

  @Test
  fun `should request storage permission on handleAddToCollectionClick call failure due to missing read storage permission`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleAddToCollectionClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verify(permissionsChecker).isReadPermissionAvailable()
    verify(detailView).requestStoragePermission(ADD_TO_COLLECTION)
  }

  @Test
  fun `should request storage permission on handleAddToCollectionClick call failure due to missing write storage permission`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(true)
    `when`(permissionsChecker.isWritePermissionAvailable()).thenReturn(false)

    detailPresenterImpl.handleAddToCollectionClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).requestStoragePermission(ADD_TO_COLLECTION)
  }

  @Test
  fun `should show no internet error on handleAddToCollectionClick call failure due to no internet availability`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleAddToCollectionClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
  }

  @Test
  fun `should update progress on handleAddToCollectionClick call success of search image type with progress value at 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handleAddToCollectionClick()

    assertTrue(detailPresenterImpl.isDownloadInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verify(detailView).getScope()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should update progress on handleAddToCollectionClick call success of wallpaper image type with progress value at 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handleAddToCollectionClick()

    assertTrue(detailPresenterImpl.isDownloadInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show adding to collection message on handleAddToCollectionClick call success of search image type with progress value at 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.adding_image_to_collections_message))
        .thenReturn(randomString)

    detailPresenterImpl.handleAddToCollectionClick()

    assertFalse(detailPresenterImpl.isDownloadInProgress)
    assertTrue(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(resourceUtils).getStringResource(R.string.adding_image_to_collections_message)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show adding to collection message on handleAddToCollectionClick call success of wallpaper image type with progress value at 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.adding_image_to_collections_message))
        .thenReturn(randomString)

    detailPresenterImpl.handleAddToCollectionClick()

    assertFalse(detailPresenterImpl.isDownloadInProgress)
    assertTrue(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(resourceUtils).getStringResource(R.string.adding_image_to_collections_message)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should add image to collection on handleAddToCollectionClick call success of search image type with progress value at 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.lastImageOperationType = CollectionsImageType.SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(imageOptionsUseCase.addImageToCollection(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink,
      detailPresenterImpl.lastImageOperationType
    )).thenReturn(
      Completable.complete())

    detailPresenterImpl.handleAddToCollectionClick()

    assertFalse(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(imageOptionsUseCase).addImageToCollection(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink,
      detailPresenterImpl.lastImageOperationType)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideScreenBlur()
    verify(detailView).showAddToCollectionSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should add image to collection on handleAddToCollectionClick call success of wallpaper image type with progress value at 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.lastImageOperationType = CollectionsImageType.WALLPAPER
    detailPresenterImpl.wallpaperImage =
        ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(imageOptionsUseCase.addImageToCollection(
      detailPresenterImpl.wallpaperImage.imageLink.large,
      detailPresenterImpl.lastImageOperationType
    )).thenReturn(Completable.complete())

    detailPresenterImpl.handleAddToCollectionClick()

    assertFalse(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(imageOptionsUseCase).addImageToCollection(
      detailPresenterImpl.wallpaperImage.imageLink.large,
      detailPresenterImpl.lastImageOperationType)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideScreenBlur()
    verify(detailView).showAddToCollectionSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should show permission required message on handlePermissionRequestResult of add to collection type call failure`() {
    detailPresenterImpl.handlePermissionRequestResult(ADD_TO_COLLECTION.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_DENIED))

    verify(detailView).showPermissionRequiredMessage()
    verify(detailView).showPermissionRequiredRationale()
  }

  @Test
  fun `should update progress on handlePermissionRequestResult of add to collection call success of search image type with progress value at 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handlePermissionRequestResult(ADD_TO_COLLECTION.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertTrue(detailPresenterImpl.isDownloadInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verify(detailView).getScope()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should update progress on handlePermissionRequestResult of add to collection call success of wallpaper image type with progress value at 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handlePermissionRequestResult(ADD_TO_COLLECTION.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertTrue(detailPresenterImpl.isDownloadInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show adding to collection message on handlePermissionRequestResult of add to collection call success of search image type with progress value at 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.adding_image_to_collections_message))
        .thenReturn(randomString)

    detailPresenterImpl.handlePermissionRequestResult(ADD_TO_COLLECTION.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertFalse(detailPresenterImpl.isDownloadInProgress)
    assertTrue(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(resourceUtils).getStringResource(R.string.adding_image_to_collections_message)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show adding to collection message on handlePermissionRequestResult of add to collection call success of wallpaper image type with progress value at 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.adding_image_to_collections_message))
        .thenReturn(randomString)

    detailPresenterImpl.handlePermissionRequestResult(ADD_TO_COLLECTION.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertFalse(detailPresenterImpl.isDownloadInProgress)
    assertTrue(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(resourceUtils).getStringResource(R.string.adding_image_to_collections_message)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should add image to collection on handlePermissionRequestResult of add to collection type call success of search image type with progress value at 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.lastImageOperationType = CollectionsImageType.SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(imageOptionsUseCase.addImageToCollection(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink,
      detailPresenterImpl.lastImageOperationType
    )).thenReturn(
      Completable.complete())

    detailPresenterImpl.handlePermissionRequestResult(ADD_TO_COLLECTION.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertFalse(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(imageOptionsUseCase).addImageToCollection(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink,
      detailPresenterImpl.lastImageOperationType)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideScreenBlur()
    verify(detailView).showAddToCollectionSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should add image to collection on handlePermissionRequestResult of add to collection type call success of wallpaper image type with progress value at 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.lastImageOperationType = CollectionsImageType.WALLPAPER
    detailPresenterImpl.wallpaperImage =
        ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(imageOptionsUseCase.addImageToCollection(
      detailPresenterImpl.wallpaperImage.imageLink.large,
      detailPresenterImpl.lastImageOperationType
    )).thenReturn(Completable.complete())

    detailPresenterImpl.handlePermissionRequestResult(ADD_TO_COLLECTION.ordinal, arrayOf(""),
      intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertFalse(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(imageOptionsUseCase).addImageToCollection(
      detailPresenterImpl.wallpaperImage.imageLink.large,
      detailPresenterImpl.lastImageOperationType)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideScreenBlur()
    verify(detailView).showAddToCollectionSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should show purchase unsuccessful message on handleViewResult of add to collection type call failure`() {
    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal, UNSUCCESSFUL_PURCHASE_CODE)

    verify(detailView).showUnsuccessfulPurchaseError()
  }

  @Test
  fun `should update progress on handleViewResult of add to collection call success of search image type with progress value at 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    assertTrue(detailPresenterImpl.isDownloadInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verify(detailView).getScope()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should update progress on handleViewResult of add to collection call success of wallpaper image type with progress value at 98`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    assertTrue(detailPresenterImpl.isDownloadInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show adding to collection message on handleViewResult of add to collection call success of search image type with progress value at 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.adding_image_to_collections_message))
        .thenReturn(randomString)

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    assertFalse(detailPresenterImpl.isDownloadInProgress)
    assertTrue(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(resourceUtils).getStringResource(R.string.adding_image_to_collections_message)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show adding to collection message on handleViewResult of add to collection call success of wallpaper image type with progress value at 99`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(resourceUtils.getStringResource(R.string.adding_image_to_collections_message))
        .thenReturn(randomString)

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    assertFalse(detailPresenterImpl.isDownloadInProgress)
    assertTrue(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(resourceUtils).getStringResource(R.string.adding_image_to_collections_message)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should add image to collection on handleViewResult of add to collection type call success of search image type with progress value at 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.lastImageOperationType = CollectionsImageType.SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(imageOptionsUseCase.addImageToCollection(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink,
      detailPresenterImpl.lastImageOperationType
    )).thenReturn(
      Completable.complete())

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    assertFalse(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(imageOptionsUseCase).addImageToCollection(
      detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink,
      detailPresenterImpl.lastImageOperationType)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideScreenBlur()
    verify(detailView).showAddToCollectionSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should add image to collection on handleViewResult of add to collection type call success of wallpaper image type with progress value at 100`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.lastImageOperationType = CollectionsImageType.WALLPAPER
    detailPresenterImpl.wallpaperImage =
        ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    stubGrantStoragePermissions()
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
      Observable.create {
        it.onNext(imageDownloadModel)
      })
    `when`(imageOptionsUseCase.addImageToCollection(
      detailPresenterImpl.wallpaperImage.imageLink.large,
      detailPresenterImpl.lastImageOperationType
    )).thenReturn(Completable.complete())

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
      PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE)

    assertFalse(detailPresenterImpl.isImageOperationInProgress)
    verify(userPremiumStatusUseCase).isUserPremium()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
      detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(imageOptionsUseCase).addImageToCollection(
      detailPresenterImpl.wallpaperImage.imageLink.large,
      detailPresenterImpl.lastImageOperationType)
    verifyStoragePermissionsCheckerCall()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideScreenBlur()
    verify(detailView).showAddToCollectionSuccessMessage()
    verify(detailView).getScope()
    verifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should set isSlidingPanelExpanded to true on setSlidingPanelStateAsExpanded call success`() {
    detailPresenterImpl.setPanelStateAsExpanded()

    assertTrue(detailPresenterImpl.isSlidingPanelExpanded)
  }

  @Test
  fun `should set isSlidingPanelExpanded to false on setSlidingPanelStateAsCollapsed call success`() {
    detailPresenterImpl.setPanelStateAsCollapsed()

    assertTrue(!detailPresenterImpl.isSlidingPanelExpanded)
  }

  @Test
  fun `should collapse sliding panel on handleImageViewClicked call success when sliding panel is in expanded state`() {
    detailPresenterImpl.isSlidingPanelExpanded = true

    detailPresenterImpl.handleBackButtonClick()

    verify(detailView).collapseSlidingPanel()
  }

  @Test
  fun `should show full screen image on handleImageViewClicked call success of search image type`() {
    detailPresenterImpl.isSlidingPanelExpanded = false
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = false
    val searchImage = SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    detailPresenterImpl.searchImage = searchImage

    detailPresenterImpl.handleImageViewClicked()

    verify(detailView).showExpandedImage(searchImage.imageQualityUrlPresenterEntity.smallImageLink,
      searchImage.imageQualityUrlPresenterEntity.largeImageLink)
  }

  @Test
  fun `should show full screen image on handleImageViewClicked call success of wallpaper image type`() {
    detailPresenterImpl.isSlidingPanelExpanded = false
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.imageHasBeenCrystallized = false
    detailPresenterImpl.imageHasBeenEdited = false
    val wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    detailPresenterImpl.wallpaperImage = wallpaperImage

    detailPresenterImpl.handleImageViewClicked()

    verify(detailView).showExpandedImage(wallpaperImage.imageLink.thumb,
      wallpaperImage.imageLink.large)
  }

  @Test
  fun `should show full screen crystallized image on handleImageViewClicked call success and image has been crystallized `() {
    detailPresenterImpl.isSlidingPanelExpanded = false
    detailPresenterImpl.lastImageOperationType = CRYSTALLIZED

    detailPresenterImpl.handleImageViewClicked()

    verify(detailView).showCrystallizedExpandedImage()
  }

  @Test
  fun `should show full screen edited image on handleImageViewClicked call success and image has been crystallized `() {
    detailPresenterImpl.isSlidingPanelExpanded = false
    detailPresenterImpl.lastImageOperationType = EDITED

    detailPresenterImpl.handleImageViewClicked()

    verify(detailView).showEditedExpandedImage()
  }

  @After
  fun tearDown() {
    detailPresenterImpl.detachView()
    verifyNoMoreInteractions(postExecutionThread, wallpaperSetter, userPremiumStatusUseCase,
      imageOptionsUseCase, detailView, resourceUtils, mockUri, mockBitmap,
      permissionsChecker)
  }

  private fun stubGrantStoragePermissions() {
    `when`(permissionsChecker.isReadPermissionAvailable()).thenReturn(true)
    `when`(permissionsChecker.isWritePermissionAvailable()).thenReturn(true)
  }

  private fun stubPostExecutionThreadReturnsIoScheduler() {
    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  private fun verifyStoragePermissionsCheckerCall() {
    verify(permissionsChecker).isReadPermissionAvailable()
    verify(permissionsChecker).isWritePermissionAvailable()
  }

  private fun verifyPostExecutionThreadSchedulerCall(times: Int = 1) {
    verify(postExecutionThread, times(times)).scheduler
  }

}