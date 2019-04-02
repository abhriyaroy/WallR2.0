package zebrostudio.wallr100.presentation

import android.Manifest.permission
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import com.yalantis.ucrop.UCrop.REQUEST_CROP
import com.yalantis.ucrop.UCrop.RESULT_ERROR
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
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
import zebrostudio.wallr100.R
import zebrostudio.wallr100.android.ui.buypro.PurchaseTransactionConfig
import zebrostudio.wallr100.android.ui.detail.images.DetailActivity
import zebrostudio.wallr100.android.utils.GsonProvider
import zebrostudio.wallr100.android.utils.WallpaperSetter
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.domain.model.imagedownload.ImageDownloadModel
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.SEARCH
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.WALLPAPERS
import zebrostudio.wallr100.presentation.datafactory.ImagePresenterEntityFactory
import zebrostudio.wallr100.presentation.datafactory.SearchPicturesPresenterEntityFactory
import zebrostudio.wallr100.presentation.detail.images.ActionType.ADD_TO_COLLECTION
import zebrostudio.wallr100.presentation.detail.images.ActionType.CRYSTALLIZE
import zebrostudio.wallr100.presentation.detail.images.ActionType.DOWNLOAD
import zebrostudio.wallr100.presentation.detail.images.ActionType.EDIT_SET
import zebrostudio.wallr100.presentation.detail.images.ActionType.QUICK_SET
import zebrostudio.wallr100.presentation.detail.images.ActionType.SHARE
import zebrostudio.wallr100.presentation.detail.images.DetailContract
import zebrostudio.wallr100.presentation.detail.images.DetailPresenterImpl
import zebrostudio.wallr100.presentation.detail.images.mapper.ImageDownloadPresenterEntityMapper
import java.util.Random
import java.util.UUID.randomUUID

const val UNSUCCESSFUL_PURCHASE_CODE = 0

@RunWith(MockitoJUnitRunner::class)
class DetailPresenterImplTest {

  @Mock private lateinit var imageOptionsUseCase: ImageOptionsUseCase
  @Mock private lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase
  @Mock private lateinit var detailView: DetailContract.DetailView
  @Mock private lateinit var wallpaperSetter: WallpaperSetter
  @Mock private lateinit var mockBitmap: Bitmap
  @Mock private lateinit var mockContext: Context
  @Mock private lateinit var postExecutionThread: PostExecutionThread
  @Mock private lateinit var gsonProvider: GsonProvider
  @Mock private lateinit var mockUri: Uri
  @Mock private lateinit var mockIntent: Intent
  @Mock private lateinit var mockBundle: Bundle
  private lateinit var detailPresenterImpl: DetailPresenterImpl
  private lateinit var testScopeProvider: TestLifecycleScopeProvider
  private val downloadProgressCompletedValue: Long = 100
  private val downloadProgressCompleteUpTo99: Long = 99
  private val downloadProgressCompleteUpTo98: Long = 98
  private val indefiniteLoaderMessage = "Finalizing wallpaper..."
  private var randomString = randomUUID().toString()
  private var randomInt = Random().nextInt()
  private lateinit var imageDownloadPresenterEntityMapper: ImageDownloadPresenterEntityMapper

  @Before
  fun setup() {
    imageDownloadPresenterEntityMapper =
        ImageDownloadPresenterEntityMapper()
    detailPresenterImpl =
        DetailPresenterImpl(mockContext,
            imageOptionsUseCase, userPremiumStatusUseCase,
            wallpaperSetter, postExecutionThread, imageDownloadPresenterEntityMapper, gsonProvider)
    detailPresenterImpl.attachView(detailView)

    testScopeProvider = TestLifecycleScopeProvider.createInitial(
        TestLifecycleScopeProvider.TestLifecycle.STARTED)
    `when`(detailView.getScope()).thenReturn(testScopeProvider)
    stubPostExecutionThreadReturnsIoScheduler()
  }

  @Test
  fun `should show search image details on setImageType as search call`() {
    val searchImagePresenterEntity =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(mockIntent.extras).thenReturn(mockBundle)
    `when`(mockIntent.extras!!.getInt(DetailActivity.IMAGE_TYPE_TAG)).thenReturn(SEARCH.ordinal)
    `when`(detailView.getSearchImageDetails()).thenReturn(searchImagePresenterEntity)

    detailPresenterImpl.setCalledIntent(mockIntent)

    assertEquals(SEARCH, detailPresenterImpl.imageType)
    verify(detailView).getSearchImageDetails()
    verify(detailView).showAuthorDetails(searchImagePresenterEntity.userPresenterEntity.name,
        searchImagePresenterEntity.userPresenterEntity.profileImageLink)
    verify(detailView).showImage(
        searchImagePresenterEntity.imageQualityUrlPresenterEntity.smallImageLink,
        searchImagePresenterEntity.imageQualityUrlPresenterEntity.largeImageLink)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show wallpaper image details on setIMAGE_TYPE_TAG as wallpaper call`() {
    val imagePresenterEntity = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(mockIntent.extras).thenReturn(mockBundle)
    `when`(mockIntent.extras!!.getInt(DetailActivity.IMAGE_TYPE_TAG)).thenReturn(WALLPAPERS.ordinal)
    `when`(detailView.getWallpaperImageDetails()).thenReturn(imagePresenterEntity)

    detailPresenterImpl.setCalledIntent(mockIntent)

    assertEquals(WALLPAPERS, detailPresenterImpl.imageType)
    verify(detailView).getWallpaperImageDetails()
    verify(detailView).showAuthorDetails(imagePresenterEntity.author.name,
        imagePresenterEntity.author.profileImageLink)
    verify(detailView).showImage(imagePresenterEntity.imageLink.thumb,
        imagePresenterEntity.imageLink.large)
    verifyNoMoreInteractions(detailView)
  }

  @Test fun `should show error toast on high quality image loading failure`() {
    detailPresenterImpl.handleHighQualityImageLoadFailed()

    verify(detailView).showImageLoadError()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should request storage permission on handleQuickSetClicked call`() {
    `when`(detailView.hasStoragePermission()).thenReturn(false)

    detailPresenterImpl.handleQuickSetClick()

    verify(detailView).hasStoragePermission()
    verify(detailView).requestStoragePermission(QUICK_SET)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show no internet error on handleQuickSetClicked call failure due to no internet`() {
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleQuickSetClick()

    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
    verifyNoMoreInteractions(detailView)
  }

  @Test fun `should show image download progress on handleQuickSetClicked call success`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
        Observable.create {
          it.onNext(imageDownloadModel)
        })

    detailPresenterImpl.handleQuickSetClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, true)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verifyNoMoreInteractions(detailView)
    verify(postExecutionThread).scheduler
    verifyNoMoreInteractions(postExecutionThread)
  }

  @Test
  fun `should show no internet error on handleShareClicked call failure due to no internet`() {
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleShareClick()

    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetToShareError()
    verifyNoMoreInteractions(detailView)
  }

  @Test fun `should redirect to pro when handleShareClicked call failure due to non pro user`() {
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    detailPresenterImpl.handleShareClick()

    verify(detailView).internetAvailability()
    verify(detailView).redirectToBuyPro(SHARE.ordinal)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should unsuccessful purchase error after handleShareClick is called and premium purchase is unsuccessful`() {
    detailPresenterImpl.handleViewResult(SHARE.ordinal, RESULT_ERROR, mockIntent)

    verify(detailView).showUnsuccessfulPurchaseError()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show no internet error due to no internet after handleShareClick is called and premium purchase is successful`() {
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleViewResult(SHARE.ordinal,
        PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, mockIntent)

    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetToShareError()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show purchase unsuccessful message on handleViewResult of share type call failure`() {
    detailPresenterImpl.handleViewResult(SHARE.ordinal, UNSUCCESSFUL_PURCHASE_CODE, null)

    verify(detailView).showUnsuccessfulPurchaseError()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show permission required message when handlePermissionRequestResult is called after permission is denied`() {
    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
        arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
        intArrayOf(PackageManager.PERMISSION_DENIED))

    verify(detailView).showPermissionRequiredMessage()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should handle permission granted success and show progress after handlePermissionRequestResult is called in handleQuickSet call`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(detailView.hasStoragePermission()).thenReturn(true)
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
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should handle permission granted success and show finalizing wallpaper message after handlePermissionRequestResult is called in handleQuickSet call`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = WALLPAPERS
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
        Observable.create {
          it.onNext(imageDownloadModel)
        })
    `when`(mockContext.getString(R.string.detail_activity_finalizing_wallpaper_messsage))
        .thenReturn(indefiniteLoaderMessage)

    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
        arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
        intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    assertEquals(detailPresenterImpl.isImageOperationInProgress, true)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(indefiniteLoaderMessage)
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should handle permission granted success and set wallpaper successfully after handlePermissionRequestResult is called`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
        Observable.just(imageDownloadModel))
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(wallpaperSetter.setWallpaper(mockBitmap)).thenReturn(true)
    `when`(mockContext.getString(R.string.detail_activity_finalizing_wallpaper_messsage))
        .thenReturn(indefiniteLoaderMessage)

    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
        arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
        intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertEquals(detailPresenterImpl.isImageOperationInProgress, false)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).showIndefiniteLoader(indefiniteLoaderMessage)
    verify(detailView).showWallpaperSetSuccessMessage()
    verify(detailView).hideScreenBlur()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should handle permission granted success and show set wallpaper error message after handlePermissionRequestResult is called`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
        Observable.just(imageDownloadModel))
    `when`(wallpaperSetter.setWallpaper(mockBitmap)).thenReturn(false)
    `when`(
        mockContext.getString(R.string.detail_activity_finalizing_wallpaper_messsage))
        .thenReturn(randomString)

    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
        arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
        intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertEquals(detailPresenterImpl.isImageOperationInProgress, false)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).showWallpaperSetErrorMessage()
    verify(detailView).hideScreenBlur()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should cancel download on handleBackButtonClick call while download in progress`() {
    detailPresenterImpl.isDownloadInProgress = true

    detailPresenterImpl.handleBackButtonClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    verify(imageOptionsUseCase).cancelFetchImageOperation()
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).hideScreenBlur()
    verify(detailView).showDownloadWallpaperCancelledMessage()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should cancel download when handleBackButtonClick is called while image downloading is in progress`() {
    detailPresenterImpl.isDownloadInProgress = true

    detailPresenterImpl.handleBackButtonClick()

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    verify(imageOptionsUseCase).cancelFetchImageOperation()
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).hideScreenBlur()
    verify(detailView).showDownloadWallpaperCancelledMessage()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show wait message when handleBackButtonClick is called while image operation is in progress`() {
    detailPresenterImpl.isImageOperationInProgress = true

    detailPresenterImpl.handleBackButtonClick()

    verify(detailView).showWallpaperOperationInProgressWaitMessage()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should exit activity when handleBackButtonClick is called and clearing cache is successful`() {
    detailPresenterImpl.isDownloadInProgress = false
    detailPresenterImpl.isImageOperationInProgress = false
    `when`(imageOptionsUseCase.clearCachesCompletable()).thenReturn(Completable.complete())

    detailPresenterImpl.handleBackButtonClick()

    verify(detailView).getScope()
    verify(detailView).exitView()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should exit activity when handleBackButtonClick is called and clearing cache is unsuccessful`() {
    detailPresenterImpl.isDownloadInProgress = false
    detailPresenterImpl.isImageOperationInProgress = false
    `when`(imageOptionsUseCase.clearCachesCompletable()).thenReturn(Completable.error(Exception()))

    detailPresenterImpl.handleBackButtonClick()

    verify(detailView).getScope()
    verify(detailView).exitView()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should handle permission granted success and show progress after handlePermissionRequestResult is called in handleEditSet call`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(detailView.hasStoragePermission()).thenReturn(true)
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
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should handle permission granted success and show indefinite loader after handlePermissionRequestResult is called in handleEditSet call`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    detailPresenterImpl.imageType = WALLPAPERS
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
        Observable.create {
          it.onNext(imageDownloadModel)
        })
    `when`(mockContext.getString(R.string.detail_activity_editing_tool_message))
        .thenReturn(randomString)

    detailPresenterImpl.handlePermissionRequestResult(EDIT_SET.ordinal,
        arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
        intArrayOf(PackageManager.PERMISSION_GRANTED))

    assertEquals(detailPresenterImpl.isDownloadInProgress, false)
    assertEquals(detailPresenterImpl.isImageOperationInProgress, true)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should handle permission granted success and start cropping activity after handlePermissionRequestResult is called in handleEditSet call`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, null)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
        Observable.create {
          it.onNext(imageDownloadModel)
        })
    `when`(imageOptionsUseCase.getCroppingSourceUri()).thenReturn(mockUri)
    `when`(imageOptionsUseCase.getCroppingDestinationUri()).thenReturn(mockUri)
    `when`(wallpaperSetter.getDesiredMinimumHeight()).thenReturn(randomInt)
    `when`(wallpaperSetter.getDesiredMinimumWidth()).thenReturn(randomInt)

    detailPresenterImpl.handlePermissionRequestResult(EDIT_SET.ordinal,
        arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
        intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).getScope()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).startCroppingActivity(imageOptionsUseCase.getCroppingSourceUri(),
        imageOptionsUseCase.getCroppingDestinationUri(),
        wallpaperSetter.getDesiredMinimumWidth(),
        wallpaperSetter.getDesiredMinimumHeight())
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should set wallpaper and show success message when crop activity results to success`() {
    `when`(detailView.getUriFromIntent(mockIntent)).thenReturn(mockUri)
    `when`(mockContext.getString(R.string.detail_activity_finalizing_wallpaper_messsage))
        .thenReturn(randomString)
    `when`(imageOptionsUseCase.getBitmapFromUriSingle(mockUri)).thenReturn(Single.just(mockBitmap))
    `when`(wallpaperSetter.setWallpaper(mockBitmap)).thenReturn(true)

    detailPresenterImpl.handleViewResult(REQUEST_CROP, RESULT_OK, mockIntent)

    assertEquals(false, detailPresenterImpl.isImageOperationInProgress)
    verify(detailView).getUriFromIntent(mockIntent)
    verify(detailView).blurScreen()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).getScope()
    verify(detailView).showImage(mockBitmap)
    verify(detailView).showWallpaperSetSuccessMessage()
    verify(detailView).hideScreenBlur()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).getBitmapFromUriSingle(mockUri)
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should show wallpaper setting error message when crop activity results to success`() {
    `when`(detailView.getUriFromIntent(mockIntent)).thenReturn(mockUri)
    `when`(mockContext.getString(R.string.detail_activity_finalizing_wallpaper_messsage))
        .thenReturn(randomString)
    `when`(imageOptionsUseCase.getBitmapFromUriSingle(mockUri)).thenReturn(Single.just(mockBitmap))
    `when`(wallpaperSetter.setWallpaper(mockBitmap)).thenReturn(false)

    detailPresenterImpl.handleViewResult(REQUEST_CROP, RESULT_OK, mockIntent)

    assertEquals(false, detailPresenterImpl.isImageOperationInProgress)
    verify(detailView).getUriFromIntent(mockIntent)
    verify(detailView).blurScreen()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).getScope()
    verify(detailView).showWallpaperSetErrorMessage()
    verify(detailView).hideScreenBlur()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).getBitmapFromUriSingle(mockUri)
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error message when crop activity results to success but getBitmapFromUriSingle call fails`() {
    `when`(detailView.getUriFromIntent(mockIntent)).thenReturn(mockUri)
    `when`(mockContext.getString(R.string.detail_activity_finalizing_wallpaper_messsage))
        .thenReturn(randomString)
    `when`(imageOptionsUseCase.getBitmapFromUriSingle(mockUri))
        .thenReturn(Single.error(Exception()))

    detailPresenterImpl.handleViewResult(REQUEST_CROP, RESULT_OK, mockIntent)

    assertEquals(false, detailPresenterImpl.isImageOperationInProgress)
    verify(detailView).getUriFromIntent(mockIntent)
    verify(detailView).blurScreen()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).getScope()
    verify(detailView).showGenericErrorMessage()
    verify(detailView).hideScreenBlur()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).getBitmapFromUriSingle(mockUri)
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should show generic error message when crop activity results to failure`() {
    `when`(detailView.getUriFromIntent(mockIntent)).thenReturn(mockUri)
    `when`(mockContext.getString(R.string.detail_activity_finalizing_wallpaper_messsage))
        .thenReturn(randomString)
    `when`(imageOptionsUseCase.getBitmapFromUriSingle(mockUri))
        .thenReturn(Single.error(Exception()))

    detailPresenterImpl.handleViewResult(REQUEST_CROP, RESULT_OK, mockIntent)

    assertEquals(false, detailPresenterImpl.isImageOperationInProgress)
    verify(detailView).getUriFromIntent(mockIntent)
    verify(detailView).blurScreen()
    verify(detailView).showIndefiniteLoader(randomString)
    verify(detailView).getScope()
    verify(detailView).showGenericErrorMessage()
    verify(detailView).hideScreenBlur()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).getBitmapFromUriSingle(mockUri)
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should redirect to buy pro on handleDownloadClick call failure`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    verify(detailView).redirectToBuyPro(DOWNLOAD.ordinal)
    verifyNoMoreInteractions(detailView)
  }

  @Test fun `should request storage permission on handleDownloadClick call failure`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(false)

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    verify(detailView).hasStoragePermission()
    verify(detailView).requestStoragePermission(DOWNLOAD)
    verifyNoMoreInteractions(detailView)
  }

  @Test fun `should show no internet error message on handleDownloadClick call failure`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show download dialog with crystallized search image option on handleDownloadClick call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = true

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).showSearchTypeDownloadDialog(true)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show download dialog without crystallized search image option on handleDownloadClick call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = false

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).showSearchTypeDownloadDialog(false)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show download dialog with crystallized wallpaper image option on handleDownloadClick call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.imageHasBeenCrystallized = true

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).showWallpaperTypeDownloadDialog(true)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show download dialog without crystallized wallpaper image option on handleDownloadClick call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.imageHasBeenCrystallized = false

    detailPresenterImpl.handleDownloadClick()

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).showWallpaperTypeDownloadDialog(false)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show download dialog with crystallized wallpaper image option on handlePermissionRequestResult call success`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.imageHasBeenCrystallized = true

    detailPresenterImpl.handlePermissionRequestResult(DOWNLOAD.ordinal, arrayOf(""),
        intArrayOf(PackageManager.PERMISSION_GRANTED))

    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).showWallpaperTypeDownloadDialog(true)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show permission required message on handlePermissionRequestResult call failure`() {
    detailPresenterImpl.handlePermissionRequestResult(DOWNLOAD.ordinal, arrayOf(""),
        intArrayOf(PackageManager.PERMISSION_DENIED))

    verify(detailView).showPermissionRequiredMessage()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show purchase unsuccessful message on handleViewResult of download type call failure`() {
    detailPresenterImpl.handleViewResult(DOWNLOAD.ordinal, UNSUCCESSFUL_PURCHASE_CODE, null)

    verify(detailView).showUnsuccessfulPurchaseError()
    verifyNoMoreInteractions(detailView)
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showGenericErrorMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showGenericErrorMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
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
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(detailView).getScope()
    verify(detailView).showDownloadStartedMessage()
    verify(detailView).showDownloadCompletedSuccessMessage()
    verifyNoMoreInteractions(detailView)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should redirect to pro when handleCrystallizeClicked call failure due to non pro user`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    detailPresenterImpl.handleCrystallizeClick()

    verify(detailView).redirectToBuyPro(CRYSTALLIZE.ordinal)
  }

  @Test
  fun `should request storage permission on handleCrystallizeClick call`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(false)

    detailPresenterImpl.handleCrystallizeClick()

    verify(detailView).hasStoragePermission()
    verify(detailView).requestStoragePermission(CRYSTALLIZE)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show no internet error on handleCrystallizeClick call failure due to no internet`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleCrystallizeClick()

    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show crystallize description dialog on handleCrystallizeClick first call`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(false)

    detailPresenterImpl.handleCrystallizeClick()

    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).showCrystallizeDescriptionDialog()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verifyNoMoreInteractions(imageOptionsUseCase)
  }

  @Test
  fun `should set bitmap to image view on handleCrystallize call success of search type`() {
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
        Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
        Single.just(Pair(true, mockBitmap)))

    detailPresenterImpl.handleCrystallizeClick()

    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showImage(mockBitmap)
    verify(detailView).hideScreenBlur()
    verify(detailView).showCrystallizeSuccessMessage()
    verify(detailView).getScope()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should show purchase unsuccessful message on handleViewResult of crystallize type call failure`() {
    detailPresenterImpl.handleViewResult(CRYSTALLIZE.ordinal, UNSUCCESSFUL_PURCHASE_CODE, null)

    verify(detailView).showUnsuccessfulPurchaseError()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should set bitmap to image view on handleViewResult with crystallize request code call success of search type`() {
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
        Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
        Single.just(Pair(true, mockBitmap)))

    detailPresenterImpl.handleViewResult(CRYSTALLIZE.ordinal,
        PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, null)

    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showImage(mockBitmap)
    verify(detailView).hideScreenBlur()
    verify(detailView).showCrystallizeSuccessMessage()
    verify(detailView).getScope()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should show error message on handleViewResult with crystallize request code call failure of search type`() {
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val link = detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.isCrystallizeDescriptionDialogShown()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(link)).thenReturn(
        Observable.just(imageDownloadModel))
    `when`(imageOptionsUseCase.crystallizeImageSingle()).thenReturn(
        Single.error(Exception()))

    detailPresenterImpl.handleViewResult(CRYSTALLIZE.ordinal,
        PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, null)

    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).showGenericErrorMessage()
    verify(detailView).hideScreenBlur()
    verify(detailView).getScope()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).isCrystallizeDescriptionDialogShown()
    verify(imageOptionsUseCase).fetchImageBitmapObservable(link)
    verify(imageOptionsUseCase).crystallizeImageSingle()
    verifyNoMoreInteractions(imageOptionsUseCase)
    verify(userPremiumStatusUseCase).isUserPremium()
    verifyNoMoreInteractions(userPremiumStatusUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should redirect to pro when handleAddToCollectionClick call failure due to non pro user`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    detailPresenterImpl.handleAddToCollectionClick()

    verify(detailView).redirectToBuyPro(ADD_TO_COLLECTION.ordinal)
  }

  @Test
  fun `should request storage permission on handleAddToCollectionClick call`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(false)

    detailPresenterImpl.handleAddToCollectionClick()

    verify(detailView).hasStoragePermission()
    verify(detailView).requestStoragePermission(ADD_TO_COLLECTION)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show no internet error on handleAddToCollectionClick call failure due to no internet`() {
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleAddToCollectionClick()

    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetError()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show purchase unsuccessful message on handleViewResult of add to collection type call failure`() {
    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal, UNSUCCESSFUL_PURCHASE_CODE,
        null)

    verify(detailView).showUnsuccessfulPurchaseError()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should update progress on handleViewResult of add to collection call success of search image type`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
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
        PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, null)

    assertTrue(detailPresenterImpl.isDownloadInProgress)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verify(detailView).getScope()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
        detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall(1)
  }

  @Test
  fun `should update progress on handleViewResult of add to collection call success of wallpaper image type`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo98, null)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
        Observable.create {
          it.onNext(imageDownloadModel)
        })

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
        PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, null)

    assertTrue(detailPresenterImpl.isDownloadInProgress)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompleteUpTo98%")
    verify(detailView).getScope()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
        detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall(1)
  }

  @Test
  fun `should show adding to collection message on handleViewResult of add to collection call success of search image type`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
        Observable.create {
          it.onNext(imageDownloadModel)
        })
    `when`(mockContext.getString(
        R.string.detail_activity_adding_image_to_collections_message)).thenReturn(randomString)

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
        PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, null)

    assertFalse(detailPresenterImpl.isDownloadInProgress)
    assertTrue(detailPresenterImpl.isImageOperationInProgress)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verify(detailView).getScope()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
        detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall(1)
  }

  @Test
  fun `should show adding to collection message on handleViewResult of add to collection call success of wallpaper image type`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompleteUpTo99, null)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
        Observable.create {
          it.onNext(imageDownloadModel)
        })
    `when`(mockContext.getString(
        R.string.detail_activity_adding_image_to_collections_message)).thenReturn(randomString)

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
        PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, null)

    assertFalse(detailPresenterImpl.isDownloadInProgress)
    assertTrue(detailPresenterImpl.isImageOperationInProgress)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).updateProgressPercentage("$downloadProgressCompletedValue%")
    verify(detailView).showIndefiniteLoaderWithAnimation(randomString)
    verify(detailView).getScope()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
        detailPresenterImpl.wallpaperImage.imageLink.large)
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall(1)
  }

  @Test
  fun `should add image to collection on handleViewResult of add to collection type call success of search image type`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.searchImage =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    val jsonString = Gson().toJson(detailPresenterImpl.searchImage)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)).thenReturn(
        Observable.create {
          it.onNext(imageDownloadModel)
        })
    `when`(gsonProvider.getGson()).thenReturn(Gson())
    `when`(imageOptionsUseCase.addImageToCollection()).thenReturn(
        Completable.complete())

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
        PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, null)

    assertFalse(detailPresenterImpl.isImageOperationInProgress)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideScreenBlur()
    verify(detailView).showAddToCollectionSuccessMessage()
    verify(detailView).getScope()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
        detailPresenterImpl.searchImage.imageQualityUrlPresenterEntity.largeImageLink)
    verify(imageOptionsUseCase).addImageToCollection()
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall(2)
  }

  @Test
  fun `should add image to collection on handleViewResult of add to collection type call success of wallpaper image type`() {
    val imageDownloadModel = ImageDownloadModel(downloadProgressCompletedValue, mockBitmap)
    detailPresenterImpl.imageType = WALLPAPERS
    detailPresenterImpl.wallpaperImage =
        ImagePresenterEntityFactory.getImagePresenterEntity()
    val jsonString = Gson().toJson(detailPresenterImpl.wallpaperImage)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(true)
    `when`(detailView.hasStoragePermission()).thenReturn(true)
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(imageOptionsUseCase.fetchImageBitmapObservable(
        detailPresenterImpl.wallpaperImage.imageLink.large)).thenReturn(
        Observable.create {
          it.onNext(imageDownloadModel)
        })
    `when`(gsonProvider.getGson()).thenReturn(Gson())
    `when`(imageOptionsUseCase.addImageToCollection()).thenReturn(
        Completable.complete())

    detailPresenterImpl.handleViewResult(ADD_TO_COLLECTION.ordinal,
        PurchaseTransactionConfig.PURCHASE_SUCCESSFUL_RESULT_CODE, null)

    assertFalse(detailPresenterImpl.isImageOperationInProgress)
    verify(detailView).hasStoragePermission()
    verify(detailView).internetAvailability()
    verify(detailView).hideIndefiniteLoader()
    verify(detailView).blurScreenAndInitializeProgressPercentage()
    verify(detailView).hideScreenBlur()
    verify(detailView).showAddToCollectionSuccessMessage()
    verify(detailView).getScope()
    verifyNoMoreInteractions(detailView)
    verify(imageOptionsUseCase).fetchImageBitmapObservable(
        detailPresenterImpl.wallpaperImage.imageLink.large)
    verify(imageOptionsUseCase).addImageToCollection()
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall(2)
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
    verifyNoMoreInteractions(detailView)
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
    verifyNoMoreInteractions(detailView)
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
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show full screen crystallized image on handleImageViewClicked call success and image has been crystallized `() {
    detailPresenterImpl.isSlidingPanelExpanded = false
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = true
    detailPresenterImpl.imageHasBeenEdited = false

    detailPresenterImpl.handleImageViewClicked()

    verify(detailView).showCrystallizedExpandedImage()
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show full screen edited image on handleImageViewClicked call success and image has been crystallized `() {
    detailPresenterImpl.isSlidingPanelExpanded = false
    detailPresenterImpl.imageType = SEARCH
    detailPresenterImpl.imageHasBeenCrystallized = false
    detailPresenterImpl.imageHasBeenEdited = true

    detailPresenterImpl.handleImageViewClicked()

    verify(detailView).showEditedExpandedImage()
    verifyNoMoreInteractions(detailView)
  }

  @After
  fun cleanup() {
    detailPresenterImpl.detachView()
  }

  private fun stubPostExecutionThreadReturnsIoScheduler() {
    whenever(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  private fun shouldVerifyPostExecutionThreadSchedulerCall(times: Int = 1) {
    verify(postExecutionThread, times(times)).scheduler
    verifyNoMoreInteractions(postExecutionThread)
  }

}