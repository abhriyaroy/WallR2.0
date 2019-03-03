package zebrostudio.wallr100.presentation

import android.graphics.Bitmap
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.uber.autodispose.lifecycle.TestLifecycleScopeProvider
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.CRYSTALLIZED_BITMAP_CACHE
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.EDITED_BITMAP_CACHE
import zebrostudio.wallr100.android.ui.expandimage.ImageLoadingType.REMOTE
import zebrostudio.wallr100.domain.executor.PostExecutionThread
import zebrostudio.wallr100.domain.interactor.ImageOptionsUseCase
import zebrostudio.wallr100.presentation.expandimage.FullScreenImageContract.FullScreenImageView
import zebrostudio.wallr100.presentation.expandimage.FullScreenImagePresenterImpl
import java.util.UUID.randomUUID

@RunWith(MockitoJUnitRunner::class)
class FullScreenImagePresenterImplTest {

  @Mock private lateinit var imageOptionsUseCase: ImageOptionsUseCase
  @Mock private lateinit var postExecutionThread: PostExecutionThread
  @Mock private lateinit var fullScreenImageView: FullScreenImageView
  @Mock private lateinit var mockBitmap: Bitmap
  private lateinit var fullScreenImagePresenter: FullScreenImagePresenterImpl
  private lateinit var testScopeProvider: TestLifecycleScopeProvider
  private var randomString = randomUUID().toString()

  @Before
  fun setup() {
    fullScreenImagePresenter =
        FullScreenImagePresenterImpl(imageOptionsUseCase, postExecutionThread)
    testScopeProvider = TestLifecycleScopeProvider.createInitial(
        TestLifecycleScopeProvider.TestLifecycle.STARTED)
    `when`(postExecutionThread.scheduler).thenReturn(Schedulers.trampoline())
  }

  @Test fun `should get image links on attachView call success of original image type`() {
    `when`(fullScreenImageView.getImageLoadingType()).thenReturn(REMOTE.ordinal)

    fullScreenImagePresenter.attachView(fullScreenImageView)

    verify(fullScreenImageView).getImageLoadingType()
    verify(fullScreenImageView).getImageLinksFromBundle()
    verifyNoMoreInteractions(fullScreenImageView)
  }

  @Test
  fun `should show crystallized image on attachView call success of crystallized image type`() {
    `when`(fullScreenImageView.getImageLoadingType()).thenReturn(
        CRYSTALLIZED_BITMAP_CACHE.ordinal)
    `when`(imageOptionsUseCase.getCrystallizedImageSingle()).thenReturn(Single.just(mockBitmap))
    `when`(fullScreenImageView.getScope()).thenReturn(testScopeProvider)

    fullScreenImagePresenter.attachView(fullScreenImageView)

    verify(fullScreenImageView).getImageLoadingType()
    verify(fullScreenImageView).showLoader()
    verify(fullScreenImageView).getScope()
    verify(fullScreenImageView).showImage(mockBitmap)
    verify(fullScreenImageView).hideLoader()
    verifyNoMoreInteractions(fullScreenImageView)
    verify(imageOptionsUseCase).getCrystallizedImageSingle()
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show edited image on attachView call success of crystallized image type`() {
    `when`(fullScreenImageView.getImageLoadingType()).thenReturn(
        EDITED_BITMAP_CACHE.ordinal)
    `when`(imageOptionsUseCase.getEditedImageSingle()).thenReturn(Single.just(mockBitmap))
    `when`(fullScreenImageView.getScope()).thenReturn(testScopeProvider)

    fullScreenImagePresenter.attachView(fullScreenImageView)

    verify(fullScreenImageView).getImageLoadingType()
    verify(fullScreenImageView).showLoader()
    verify(fullScreenImageView).getScope()
    verify(fullScreenImageView).showImage(mockBitmap)
    verify(fullScreenImageView).hideLoader()
    verifyNoMoreInteractions(fullScreenImageView)
    verify(imageOptionsUseCase).getEditedImageSingle()
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test
  fun `should show generic error message on attachView call error of crystallized image type`() {
    `when`(fullScreenImageView.getImageLoadingType()).thenReturn(
        CRYSTALLIZED_BITMAP_CACHE.ordinal)
    `when`(imageOptionsUseCase.getCrystallizedImageSingle()).thenReturn(Single.error(Exception()))
    `when`(fullScreenImageView.getScope()).thenReturn(testScopeProvider)

    fullScreenImagePresenter.attachView(fullScreenImageView)

    verify(fullScreenImageView).getImageLoadingType()
    verify(fullScreenImageView).showLoader()
    verify(fullScreenImageView).getScope()
    verify(fullScreenImageView).showGenericErrorMessage()
    verify(fullScreenImageView).hideLoader()
    verifyNoMoreInteractions(fullScreenImageView)
    verify(imageOptionsUseCase).getCrystallizedImageSingle()
    verifyNoMoreInteractions(imageOptionsUseCase)
    shouldVerifyPostExecutionThreadSchedulerCall()
  }

  @Test fun `should show low quality image and loader on setLowQualityImageLink call success`() {
    `when`(fullScreenImageView.getImageLoadingType()).thenReturn(REMOTE.ordinal)

    fullScreenImagePresenter.attachView(fullScreenImageView)
    fullScreenImagePresenter.setLowQualityImageLink(randomString)

    verify(fullScreenImageView).getImageLoadingType()
    verify(fullScreenImageView).getImageLinksFromBundle()
    verify(fullScreenImageView).showLowQualityImage(randomString)
    verify(fullScreenImageView).showLoader()
    verifyNoMoreInteractions(fullScreenImageView)
  }

  @Test fun `should start loading high quality image on setHighQualityImageLink call success`() {
    `when`(fullScreenImageView.getImageLoadingType()).thenReturn(REMOTE.ordinal)

    fullScreenImagePresenter.attachView(fullScreenImageView)
    fullScreenImagePresenter.setHighQualityImageLink(randomString)

    verify(fullScreenImageView).getImageLoadingType()
    verify(fullScreenImageView).getImageLinksFromBundle()
    verify(fullScreenImageView).startLoadingHighQualityImage(randomString)
    verifyNoMoreInteractions(fullScreenImageView)
  }

  @Test
  fun `should hide loader and low quality image on notifyHighQualityImageLoadingFinished call success`() {
    `when`(fullScreenImageView.getImageLoadingType()).thenReturn(REMOTE.ordinal)

    fullScreenImagePresenter.attachView(fullScreenImageView)
    fullScreenImagePresenter.notifyHighQualityImageLoadingFinished()

    verify(fullScreenImageView).getImageLoadingType()
    verify(fullScreenImageView).getImageLinksFromBundle()
    verify(fullScreenImageView).hideLoader()
    verify(fullScreenImageView).hideLowQualityImage()
    verifyNoMoreInteractions(fullScreenImageView)
  }

  @Test
  fun `should hide loader and show loading error on notifyHighQualityImageLoadingFinished call error`() {
    `when`(fullScreenImageView.getImageLoadingType()).thenReturn(REMOTE.ordinal)

    fullScreenImagePresenter.attachView(fullScreenImageView)
    fullScreenImagePresenter.notifyHighQualityImageLoadingFailed()

    verify(fullScreenImageView).getImageLoadingType()
    verify(fullScreenImageView).getImageLinksFromBundle()
    verify(fullScreenImageView).hideLoader()
    verify(fullScreenImageView).showHighQualityImageLoadingError()
    verifyNoMoreInteractions(fullScreenImageView)
  }

  @Test
  fun `should show status bar and navigation bar on notifyPhotoViewTapped call success with isInFullScreen set to true`() {
    `when`(fullScreenImageView.getImageLoadingType()).thenReturn(REMOTE.ordinal)
    fullScreenImagePresenter.isInFullScreenMode = true

    fullScreenImagePresenter.attachView(fullScreenImageView)
    fullScreenImagePresenter.notifyPhotoViewTapped()

    verify(fullScreenImageView).getImageLoadingType()
    verify(fullScreenImageView).getImageLinksFromBundle()
    verify(fullScreenImageView).showStatusAndNavBar()
    verifyNoMoreInteractions(fullScreenImageView)
  }

  @Test
  fun `should hide status bar and navigation bar on notifyPhotoViewTapped call success with isInFullScreen set to false`() {
    `when`(fullScreenImageView.getImageLoadingType()).thenReturn(REMOTE.ordinal)
    fullScreenImagePresenter.isInFullScreenMode = false

    fullScreenImagePresenter.attachView(fullScreenImageView)
    fullScreenImagePresenter.notifyPhotoViewTapped()

    verify(fullScreenImageView).getImageLoadingType()
    verify(fullScreenImageView).getImageLinksFromBundle()
    verify(fullScreenImageView).hideStatusAndNavBar()
    verifyNoMoreInteractions(fullScreenImageView)
  }

  @Test fun `should set isInFullScreen to false on notifyStatusBarAndNavBarShown call success`() {
    fullScreenImagePresenter.notifyStatusBarAndNavBarShown()

    assertFalse(fullScreenImagePresenter.isInFullScreenMode)
  }

  @Test fun `should set isInFullScreen to true on notifyStatusBarAndNavBarHidden call success`() {
    fullScreenImagePresenter.notifyStatusBarAndNavBarHidden()

    assertTrue(fullScreenImagePresenter.isInFullScreenMode)
  }

  @After
  fun tearDown() {
    fullScreenImagePresenter.detachView()
  }

  private fun shouldVerifyPostExecutionThreadSchedulerCall(times: Int = 1) {
    verify(postExecutionThread, times(times)).scheduler
    verifyNoMoreInteractions(postExecutionThread)
  }

}