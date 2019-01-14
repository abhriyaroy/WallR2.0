package zebrostudio.wallr100.presentation

import android.Manifest.*
import android.content.pm.PackageManager
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.domain.interactor.UserPremiumStatusUseCase
import zebrostudio.wallr100.presentation.adapters.ImageRecyclerViewPresenterImpl.ImageListType.*
import zebrostudio.wallr100.presentation.datafactory.ImagePresenterEntityFactory
import zebrostudio.wallr100.presentation.datafactory.SearchPicturesPresenterEntityFactory
import zebrostudio.wallr100.presentation.detail.ActionType.*
import zebrostudio.wallr100.presentation.detail.DetailContract
import zebrostudio.wallr100.presentation.detail.DetailPresenterImpl
import zebrostudio.wallr100.rules.TrampolineSchedulerRule

@RunWith(MockitoJUnitRunner::class)
class DetailActivityPresenterImplTest {

  @get:Rule var trampolineSchedulerRule = TrampolineSchedulerRule()
  @Mock private lateinit var imageOptionsUseCase: ImageOptionsUseCase
  @Mock private lateinit var userPremiumStatusUseCase: UserPremiumStatusUseCase
  @Mock private lateinit var detailView: DetailContract.DetailView
  private lateinit var detailPresenterImpl: DetailPresenterImpl

  @Before
  fun setup() {
    detailPresenterImpl = DetailPresenterImpl(imageOptionsUseCase, userPremiumStatusUseCase)
    detailPresenterImpl.attachView(detailView)
  }

  @Test
  fun `should show search image details on setImageType as search call`() {
    val searchImagePresenterEntity =
        SearchPicturesPresenterEntityFactory.getSearchPicturesPresenterEntity()
    `when`(detailView.getSearchImageDetails()).thenReturn(searchImagePresenterEntity)

    detailPresenterImpl.setImageType(SEARCH)

    verify(detailView).getSearchImageDetails()
    verify(detailView).showAuthorDetails(searchImagePresenterEntity.userPresenterEntity.name,
        searchImagePresenterEntity.userPresenterEntity.profileImageLink)
    verify(detailView).showImage(
        searchImagePresenterEntity.imageQualityUrlPresenterEntity.smallImageLink,
        searchImagePresenterEntity.imageQualityUrlPresenterEntity.largeImageLink)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show wallpaper image details on setImageType as wallpaper call`() {
    val imagePresenterEntity = ImagePresenterEntityFactory.getImagePresenterEntity()
    `when`(detailView.getWallpaperImageDetails()).thenReturn(imagePresenterEntity)

    detailPresenterImpl.setImageType(WALLPAPERS)

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
  fun `should show no internet error on notifyShareClicked call failure due to no internet`() {
    `when`(detailView.internetAvailability()).thenReturn(false)

    detailPresenterImpl.handleShareClick()

    verify(detailView).internetAvailability()
    verify(detailView).showNoInternetToShareError()
    verifyNoMoreInteractions(detailView)
  }

  @Test fun `should redirect to pro when notifyShareClicked call failure due to non pro user`() {
    `when`(detailView.internetAvailability()).thenReturn(true)
    `when`(userPremiumStatusUseCase.isUserPremium()).thenReturn(false)

    detailPresenterImpl.handleShareClick()

    verify(detailView).internetAvailability()
    verify(detailView).redirectToBuyPro(SHARE.ordinal)
    verifyNoMoreInteractions(detailView)
  }

  @Test
  fun `should show permission required message when notifyPermissionRequestResult is called after permission is denied`() {
    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
        arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
        intArrayOf(PackageManager.PERMISSION_DENIED))

    verify(detailView).showPermissionRequiredMessage()
    verifyNoMoreInteractions(detailView)
  }

  @Test fun `should handle permission granted after notifyPermissionRequestResult is called`() {
    detailPresenterImpl.handlePermissionRequestResult(QUICK_SET.ordinal,
        arrayOf(permission.READ_EXTERNAL_STORAGE, permission.WRITE_EXTERNAL_STORAGE),
        intArrayOf(PackageManager.PERMISSION_GRANTED))

    verifyNoMoreInteractions(detailView)
  }

  @After
  fun cleanup() {
    detailPresenterImpl.detachView()
  }

}